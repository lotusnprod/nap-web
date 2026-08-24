/*
 * Narrows a listing as the visitor types.
 *
 * The whole list is already in the page, see listingPage in genericSearchPage.kt, so there is
 * nothing to ask the server for: a row is kept when every word typed appears in it. Without
 * this file the form under the box still submits and lands on the server side search, which is
 * why nothing here runs before the visitor types.
 */
(function () {
    function init() {
        var input = document.getElementById('live-filter-input');
        var table = document.getElementById('live-filter-table');
        if (!input || !table) return;

        var count = document.getElementById('live-filter-count');
        var empty = document.getElementById('live-filter-empty');

        /* data-filter is the part of the row worth matching, the name rather than the counts
           next to it. Rows rendered without it fall back to everything they say. */
        var entries = Array.prototype.slice.call(table.querySelectorAll('tbody tr')).map(function (row) {
            var text = row.getAttribute('data-filter');
            if (text === null) text = row.textContent || '';
            return { el: row, text: text.toLowerCase(), visible: true };
        });

        function apply() {
            var terms = input.value.toLowerCase().split(/\s+/).filter(function (term) {
                return term !== '';
            });

            var shown = 0;
            entries.forEach(function (entry) {
                var visible = terms.every(function (term) {
                    return entry.text.indexOf(term) !== -1;
                });
                if (visible) shown++;
                /* Thousands of rows: only touch the ones that actually change. */
                if (visible !== entry.visible) {
                    entry.el.style.display = visible ? '' : 'none';
                    entry.visible = visible;
                }
            });

            if (count) count.textContent = shown;
            if (empty) empty.classList.toggle('d-none', shown !== 0);
        }

        input.addEventListener('input', apply);

        /* Enter would reload the page to search for what is already filtered here. */
        if (input.form) {
            input.form.addEventListener('submit', function (event) {
                event.preventDefault();
                apply();
            });
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
