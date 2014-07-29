
app.service('timeService',['$q', '$timeout', '$window', function($q, $timeout, $window) {

    return {

        humanReadableAbbr: function(date) {
            return moment(date).fromNow();
        }

    }
}]);

app.service('i18nService',['$locale', function($locale) {

    //TODO right now hardcoded
    var translationMap = {};

    function resolve(id) {
        var translated = translationMap[id];

        if(translated) {
            return translated;
        } else {
            return id;
        }
    }

    $locale.resolve = resolve;

    return $locale;

}]);

app.service('pagerFactory',['DEFAULT_PAGE_SIZE', function(DEFAULT_PAGE_SIZE) {

    var DEFAULT =  {currentPage: 1, pageLength:0, fullLength: 0, pages: 0, start: 0, end: 0};

    return {
        newInstance: function(currentPage, pageLength, fullLength) {
            if(!fullLength || fullLength < 1 || !currentPage || currentPage < 1) {
                return angular.copy(DEFAULT);
            }

            if(pageLength !== undefined || pageLength < 1) {
                pageLength = Math.max(DEFAULT_PAGE_SIZE, pageLength);
            } else {
                pageLength = DEFAULT_PAGE_SIZE;
            }

            var pagination = {
                currentPage: currentPage,
                fullLength: fullLength,
                pageLength: pageLength,
                start: 0,
                end: 0
            };

            pagination.pages = parseInt((fullLength + pageLength -1) / pageLength);

            if(currentPage <= pagination.pages) {
                pagination.start = (currentPage === 1) ? 1 : (currentPage -1) * pageLength;
                pagination.end = Math.min(currentPage * pageLength, fullLength);
            }

            return pagination;
        }

    }
}]);

app.service('templateService',['$templateCache', '$q', '$http', function($templateCache, $q, $http) {

    return {
        template: function(name) {
            deferred = $q.defer();

            var tmpl = $templateCache.get(name);

            if(tmpl) {
                deferred.resolve(tmpl);
            } else {
                $http.get(name)
                    .success(function(html) {
                        $templateCache.put(name, html)
                        deferred.resolve(html);
                    });
            }

            return deferred.promise;
        }
    }

}]);

app.service('folderCache', function() {
    var cache = [];

    return {
        evict : function() {
            cache = [];
        },
        fill : function(data) {
            cache = angular.copy(data);
        },
        add : function(data) {
            addSorted(data);
        },
        isEmpty : function() {
            return cache.length === 0;
        },
        snapshot : function() {
            return angular.copy(cache);
        }
    };

    function addSorted(data) {
        var copy = angular.copy(cache);
        copy.push(data);
        cache = sort(copy);
    };

    function sort(data) {
        return data.sort(function(left, right) {
            return left.id.localeCompare(right.id);
        });
    }
});
