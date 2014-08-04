
app.service('timeService',['$q', '$timeout', '$window', function($q, $timeout, $window) {

    return {
        humanReadableAbbr: function(date) {
            return moment(date).fromNow();
        },
        humanReadable: function(date) {
            return moment(date).format("LLLL");
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

app.service('folderCache', function() {
    var cache = [];

    return {
        clear : function() {
            cache = [];
        },
        evict : function(id) {
            remove(id);
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
        },
        update : function(data) {
            update(angular.copy(data));
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
    };

    function remove(id) {
        var copy = angular.copy(cache);
        for(i=0;i<=copy.length;i++) {
            if(copy[i].id === id) {
                copy.splice(i, 1);
                cache = copy;
                return;
            }
        }
    };

    function update(data) {
        var copy = angular.copy(cache);
        for(i=0;i<=copy.length;i++) {
            if(copy[i].id === data.id) {
                copy[i] = data;
                cache = copy;
                return;
            }
        }
    }
});

app.service('routeService', function($rootScope, $route, $location, $log, localStorageService) {

    $rootScope.$on('$routeChangeError', function(event, next, current) {
        $log.error(event)
    });

    $rootScope.$on('$routeChangeStart', function(event, next, current) {
        localStorageService.set('lastRoute', {'path' : current.originalPath, 'params': current.params });
        localStorageService.set('currentRoute', {'path' : next.originalPath, 'params': next.params });
    });

    var _navigate = function(route) {
        $log.info("navigate to ", route);
        $location.url(route);
    }

    var navigateToPrevious =function() {
        var url = previous();
        _navigate(url);
    };

    var previous =function() {
        var lastRoute = localStorageService.get('lastRoute');
        localStorageService.remove('lastRoute');

        if(!lastRoute) {
            return "#/box";
        }

        if(!lastRoute.params && Object.keys(lastRoute.params).length == 0) {
            return lastRoute.path;
        }
        var url = "#"+lastRoute.path + "?";
        for(k in lastRoute.params) {
            url = url + k + "=" + encodeURIComponent(lastRoute.params[k]) + "&";
        }

        $log.info("previous route ", url);
        return url;
    };

    var currentRoute = function(route) {
        var currentRoute = localStorageService.get('currentRoute');
        if(!currentRoute) {
            return false;
        }
        return currentRoute.path === "/"+route;
    };

    var navigateTo = function(route) {
        _navigate("/"+route);
    };

    return {
        navigateToPrevious : navigateToPrevious,
        previous: previous,
        currentRoute : currentRoute,
        navigateTo : navigateTo
    };
});

app.service('submissionService',['$q', '$http', 'API_HOME', function($q, $http, API_HOME) {


    var _submission = function(mail) {
        var deferred = $q.defer();

        if(!angular.isDefined(mail) && !angular.isDefined(mail.id)) {
            deferred.reject("mail id undefined");
            return;
        }

        $http({method: 'POST', url: API_HOME +'/submission/disposition/'+encodeURIComponent(mail.id)})
        .success(function() {
            deferred.resolve();
        })
        .error(function(data) {
            deferred.reject(data);
        });

        return deferred.promise;
    };

    return {

        disposition: _submission

    };

}]);