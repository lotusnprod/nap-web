/*
 * Guided tour.
 *
 * Off unless the visitor turns it on from the navbar. The steps are not defined here: they
 * are read from the [data-tour-step] elements of the page, so a page declares its own tour
 * (see tutorial.kt). Progress is kept per page in localStorage, so leaving and coming back
 * resumes where the visitor was.
 */
(function () {
    "use strict";

    var ENABLED_KEY = "nap.tutorial.enabled";
    var PROGRESS_KEY = "nap.tutorial.progress";
    var STARTED_KEY = "nap.tutorial.startedOn";

    // localStorage throws when storage is disabled or full. The tour is a nicety, it must
    // never take the page down with it, so every access goes through these two.
    function readStore(key, fallback) {
        try {
            var value = window.localStorage.getItem(key);
            return value === null ? fallback : value;
        } catch (e) {
            return fallback;
        }
    }

    function writeStore(key, value) {
        try {
            window.localStorage.setItem(key, value);
        } catch (e) {
            /* nothing we can do, the tour just will not be remembered */
        }
    }

    function isEnabled() {
        return readStore(ENABLED_KEY, "0") === "1";
    }

    /* Progress is per tour, and a tour is the first segment of the path: the home page, the
       FAQ and a results page each have their own. */
    function tourName() {
        var segment = window.location.pathname.split("/")[1];
        return segment ? segment : "home";
    }

    function readProgress() {
        try {
            return JSON.parse(readStore(PROGRESS_KEY, "{}")) || {};
        } catch (e) {
            return {};
        }
    }

    function readTourProgress() {
        var progress = readProgress()[tourName()];
        if (progress && typeof progress.step === "number") {
            return progress;
        }
        return { step: 0, done: false };
    }

    function writeTourProgress(step, done) {
        var all = readProgress();
        all[tourName()] = { step: step, done: done };
        writeStore(PROGRESS_KEY, JSON.stringify(all));
    }

    /* Shared steps describe the navbar, which is on every page. Walking through them again
       on every page someone opens is noise, so they only run on the home page and on the
       page where the tour was turned on. Navigating away leaves that page's own steps. */
    function includeShared() {
        return tourName() === "home" || readStore(STARTED_KEY, "") === tourName();
    }

    /* Steps of the current page, in order. */
    function steps() {
        var nodes = Array.prototype.slice.call(document.querySelectorAll("[data-tour-step]"));
        var shared = includeShared();
        return nodes.filter(function (node) {
            return shared || !node.hasAttribute("data-tour-shared");
        }).sort(function (a, b) {
            return Number(a.getAttribute("data-tour-step")) - Number(b.getAttribute("data-tour-step"));
        });
    }

    /* On a narrow screen the search form is collapsed behind the burger menu. Opening it is
       part of showing the step, the search box is the whole point of the tour. */
    function reveal(node) {
        var parent = node.parentElement;
        while (parent) {
            if (parent.classList.contains("collapse")) {
                parent.classList.add("show");
            }
            parent = parent.parentElement;
        }
    }

    function isVisible(node) {
        return node.offsetParent !== null || node.getClientRects().length > 0;
    }

    var card = null;
    var target = null;

    function hide() {
        if (card) {
            card.parentNode.removeChild(card);
            card = null;
        }
        if (target) {
            target.classList.remove("tour-target");
            target = null;
        }
    }

    function place() {
        if (!card || !target) {
            return;
        }
        var rect = target.getBoundingClientRect();
        card.style.top = (rect.bottom + window.pageYOffset + 8) + "px";

        var left = rect.left + window.pageXOffset;
        var rightmost = window.pageXOffset + document.documentElement.clientWidth - card.offsetWidth - 8;
        card.style.left = Math.max(window.pageXOffset + 8, Math.min(left, rightmost)) + "px";
    }

    function makeButton(label, classes, onClick) {
        var element = document.createElement("button");
        element.type = "button";
        element.className = classes;
        element.textContent = label;
        element.addEventListener("click", onClick);
        return element;
    }

    /**
     * Show one step.
     *
     * @param index Position in the list of steps of the page
     * @param direction Which way the visitor is going, so that a step with nothing to point
     *                  at is skipped without walking back over what they just read
     */
    function show(index, direction) {
        var list = steps();
        var forward = direction !== -1;
        if (!list.length) {
            hide();
            syncToggle();
            return;
        }
        if (index >= list.length) {
            finish();
            return;
        }
        if (index < 0) {
            index = 0;
            forward = true;
        }

        var node = list[index];
        reveal(node);
        if (!isVisible(node)) {
            if (!forward && index === 0) {
                hide();
                syncToggle();
                return;
            }
            show(index + (forward ? 1 : -1), direction);
            return;
        }

        hide();
        writeTourProgress(index, false);
        target = node;
        target.classList.add("tour-target");

        card = document.createElement("div");
        card.className = "tour-card";
        card.setAttribute("role", "dialog");
        card.setAttribute("aria-label", "Tutorial step");

        var title = document.createElement("p");
        title.className = "tour-card-title";
        title.textContent = target.getAttribute("data-tour-title") || "";
        card.appendChild(title);

        var body = document.createElement("p");
        body.className = "tour-card-body";
        body.textContent = target.getAttribute("data-tour-body") || "";
        card.appendChild(body);

        var footer = document.createElement("div");
        footer.className = "tour-card-footer";

        var counter = document.createElement("span");
        counter.className = "tour-card-count";
        counter.textContent = (index + 1) + " of " + list.length;
        footer.appendChild(counter);

        if (index > 0) {
            footer.appendChild(makeButton("Back", "btn btn-sm btn-outline-secondary", function () {
                show(index - 1, -1);
            }));
        }
        footer.appendChild(makeButton(index === list.length - 1 ? "Done" : "Next", "btn btn-sm btn-primary", function () {
            show(index + 1, 1);
        }));
        footer.appendChild(makeButton("Turn off", "btn btn-sm btn-link", disable));

        card.appendChild(footer);
        document.body.appendChild(card);

        place();
        target.scrollIntoView({ block: "center", behavior: "smooth" });
        syncToggle();
    }

    /* Reaching the end does not turn the tour off, it just stops this page from popping up
       again. Other pages still have their own tour to offer. */
    function finish() {
        writeTourProgress(0, true);
        hide();
        syncToggle();
    }

    function disable() {
        writeStore(ENABLED_KEY, "0");
        hide();
        syncToggle();
    }

    /* Turning it on always restarts the tour of the current page, which is what someone
       clicking a button labelled "Tutorial" is asking for. Asking for it here also earns
       this page the shared steps, see includeShared. */
    function enable() {
        writeStore(ENABLED_KEY, "1");
        writeStore(STARTED_KEY, tourName());
        writeTourProgress(0, false);
        show(0);
        syncToggle();
    }

    /* The button reports what is happening on this page, not the value of a flag: a page can
       have the tutorial enabled and still show nothing, because the tour has been walked
       through already or because the page has no step of its own. Saying "on" there would be
       true and useless, and would turn a request to run the tour here into a request to
       switch it off. */
    function syncToggle() {
        var toggle = document.getElementById("tutorial-toggle");
        if (!toggle) {
            return;
        }
        var running = card !== null;
        toggle.textContent = running ? "Tutorial: on" : "Tutorial: off";
        toggle.setAttribute("aria-pressed", running ? "true" : "false");
        toggle.classList.toggle("active", running);
    }

    function start() {
        syncToggle();

        var toggle = document.getElementById("tutorial-toggle");
        if (toggle) {
            toggle.addEventListener("click", function () {
                // Switch off what is on screen, otherwise start the tour of this page
                if (card) {
                    disable();
                } else {
                    enable();
                }
            });
        }

        if (!isEnabled()) {
            return;
        }
        var progress = readTourProgress();
        if (progress.done) {
            return;
        }

        /* The stored step can point past the end: the tour of this page was longer when it
           was left, because the shared steps were part of it and no longer are. Clamp it,
           rather than letting show() read it as "walked past the last step" and retire a
           page the visitor never finished. */
        var last = steps().length - 1;
        show(progress.step > last ? Math.max(last, 0) : progress.step);
    }

    /* Escape puts the tour away without turning it off or losing the place. */
    document.addEventListener("keydown", function (event) {
        if (card && event.key === "Escape") {
            hide();
            syncToggle();
        }
    });

    window.addEventListener("resize", place);

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", start);
    } else {
        start();
    }
})();
