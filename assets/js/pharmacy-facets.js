/*
 * Faceted filtering of the experiment tables (worktype / pharmacology).
 * Works on the rows rendered by presentPharmacyResults and the panel rendered by pharmacyFacets.
 */
(function () {
    function init() {
        var panel = document.getElementById('pharmacy-facets');
        var table = document.getElementById('pharmacy');
        if (!panel || !table) return;

        /* The facets are whatever the server decided to render, see facetsOf in pharmacyFacets.kt.
           Each of them is backed by a data-<key> attribute on the experiment rows. */
        var FACETS = Array.prototype.slice.call(panel.querySelectorAll('.facet-group')).map(function (group) {
            return group.getAttribute('data-facet');
        });
        var NONE = '__none__';

        var rows = Array.prototype.slice.call(table.querySelectorAll('tbody tr.pharmacy-row'));
        var entries = rows.map(function (row) {
            var entry = { el: row };
            FACETS.forEach(function (facet) {
                var raw = row.getAttribute('data-' + facet) || '';
                var values = raw.split('|').filter(function (v) { return v !== ''; });
                entry[facet] = values.length ? values : [NONE];
            });
            return entry;
        });

        var groups = {};
        FACETS.forEach(function (facet) {
            var group = panel.querySelector('.facet-group[data-facet="' + facet + '"]');
            if (!group) return;
            groups[facet] = {
                el: group,
                clear: group.querySelector('.facet-clear'),
                empty: group.querySelector('.facet-empty'),
                search: group.querySelector('.facet-search'),
                options: Array.prototype.slice.call(group.querySelectorAll('.facet-option')).map(function (option) {
                    return {
                        el: option,
                        value: option.getAttribute('data-value'),
                        label: option.getAttribute('data-label') || '',
                        input: option.querySelector('input'),
                        count: option.querySelector('.facet-count')
                    };
                })
            };
        });

        var count = document.getElementById('pharmacy-count');
        var noResults = document.getElementById('pharmacy-no-results');
        var clearAll = document.getElementById('facet-clear-all');

        function selection(facet) {
            var selected = [];
            var group = groups[facet];
            if (!group) return selected;
            group.options.forEach(function (option) {
                if (option.input.checked) selected.push(option.value);
            });
            return selected;
        }

        /* An experiment matches when, for every facet but `skip`, it carries at least
           one of the values selected in that facet (OR inside a facet, AND between facets). */
        function matches(entry, selections, skip) {
            return FACETS.every(function (facet) {
                if (facet === skip) return true;
                var selected = selections[facet];
                if (!selected.length) return true;
                return entry[facet].some(function (value) {
                    return selected.indexOf(value) !== -1;
                });
            });
        }

        function applySearch(facet) {
            var group = groups[facet];
            if (!group || !group.search) return;
            var needle = group.search.value.trim().toLowerCase();
            var visible = 0;
            group.options.forEach(function (option) {
                var hit = needle === '' ? true : option.label.indexOf(needle) !== -1;
                option.el.classList.toggle('d-none', !hit);
                if (hit) visible++;
            });
            if (group.empty) group.empty.classList.toggle('d-none', visible !== 0);
        }

        function apply() {
            var selections = {};
            var total = 0;
            FACETS.forEach(function (facet) {
                selections[facet] = selection(facet);
                total += selections[facet].length;
            });

            var visible = 0;
            entries.forEach(function (entry) {
                var shown = matches(entry, selections, null);
                entry.el.classList.toggle('d-none', !shown);
                if (shown) visible++;
            });

            /* Counts of a facet are computed while ignoring that facet's own selection,
               so the other choices of the facet stay reachable. */
            FACETS.forEach(function (facet) {
                var group = groups[facet];
                if (!group) return;
                var counts = {};
                entries.forEach(function (entry) {
                    if (!matches(entry, selections, facet)) return;
                    entry[facet].forEach(function (value) {
                        counts[value] = (counts[value] || 0) + 1;
                    });
                });
                group.options.forEach(function (option) {
                    var count = counts[option.value] || 0;
                    if (option.count) option.count.textContent = count;
                    var unreachable = count === 0 ? !option.input.checked : false;
                    option.el.classList.toggle('facet-unreachable', unreachable);
                });
                if (group.clear) group.clear.classList.toggle('d-none', selections[facet].length === 0);
                applySearch(facet);
            });

            if (clearAll) clearAll.classList.toggle('d-none', total === 0);
            if (noResults) noResults.classList.toggle('d-none', visible !== 0);
            if (count) {
                count.textContent = total === 0
                    ? entries.length + ' experiments'
                    : visible + ' of ' + entries.length + ' experiments';
            }

            updateUrl(selections);
        }

        function updateUrl(selections) {
            if (!window.history || !window.history.replaceState) return;
            var params = new URLSearchParams(window.location.search);
            FACETS.forEach(function (facet) {
                if (selections[facet].length) {
                    params.set(facet, selections[facet].join(','));
                } else {
                    params.delete(facet);
                }
            });
            var query = params.toString();
            window.history.replaceState(null, '', window.location.pathname + (query ? '?' + query : ''));
        }

        function restoreFromUrl() {
            var params = new URLSearchParams(window.location.search);
            FACETS.forEach(function (facet) {
                var raw = params.get(facet);
                if (!raw) return;
                var group = groups[facet];
                if (!group) return;
                var wanted = raw.split(',');
                group.options.forEach(function (option) {
                    if (wanted.indexOf(option.value) !== -1) option.input.checked = true;
                });
            });
        }

        FACETS.forEach(function (facet) {
            var group = groups[facet];
            if (!group) return;
            group.options.forEach(function (option) {
                option.input.addEventListener('change', apply);
            });
            if (group.search) {
                group.search.addEventListener('input', function () { applySearch(facet); });
            }
            if (group.clear) {
                group.clear.addEventListener('click', function () {
                    group.options.forEach(function (option) { option.input.checked = false; });
                    apply();
                });
            }
        });

        if (clearAll) {
            clearAll.addEventListener('click', function () {
                FACETS.forEach(function (facet) {
                    var group = groups[facet];
                    if (!group) return;
                    group.options.forEach(function (option) { option.input.checked = false; });
                });
                apply();
            });
        }

        restoreFromUrl();
        apply();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
