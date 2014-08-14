
app.service('timeService',function() {

    return {
        humanReadableAbbr: function(date) {
            return moment(date).fromNow();
        },
        humanReadable: function(date) {
            return moment(date).format("LLLL");
        }
    }
});

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
        if(current) {
            localStorageService.set('lastRoute', {'path' : current.originalPath, 'params': current.params });
        }
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
            return "/box";
        }

        if(!lastRoute.params || Object.keys(lastRoute.params).length == 0) {
            return lastRoute.path;
        }
        var url = lastRoute.path + "?";
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
        if(!route.params || Object.keys(route.params).length == 0) {
            return _navigate("/"+route.path);
        }

        var url = "/"+route.path+"?";
        for(k in route.params) {
            url = url + k + "=" + encodeURIComponent(route.params[k]) + "&";
        }

        _navigate(url);
    };

    return {
        navigateToPrevious : navigateToPrevious,
        previous: previous,
        currentRoute : currentRoute,
        navigateTo : navigateTo
    };
});

app.service('submissionService',['$q', '$http', 'API_HOME', function($q, $http, API_HOME) {

    var _disposition = function(mail) {
        var deferred = $q.defer();

        if(!angular.isDefined(mail) && !angular.isDefined(mail.id)) {
            deferred.reject("mail id undefined");
            return;
        }

        mail.date = new Date();

        $http({method: 'POST', url: API_HOME +'/submission/disposition/'+mail.id})
        .success(function() {
            deferred.resolve();
        })
        .error(function(data) {
            deferred.reject(data);
        });

        return deferred.promise;
    };

    var _submission = function(mail) {
        var deferred = $q.defer();

        if(!angular.isDefined(mail) && !angular.isDefined(mail.id)) {
            deferred.reject("mail id undefined");
            return;
        }

        var data = {clientMessage: mail};

        $http({method: 'POST', url: API_HOME +'submission', data: data})
            .success(function() {
                deferred.resolve();
            })
            .error(function(data) {
                deferred.reject(data);
            });

        return deferred.promise;
    };

    return {
        disposition: _disposition,
        submission: _submission
    };

}]);

app.service('composerService',['citeService', function(citeService) {

    var _createForward = function(mail) {
        var copy = angular.copy(mail);
        delete copy.id;

        delete copy.to;
        delete copy.sender;
        copy.subject = "Fwd: " + copy.subject;

        //TODO body

        return copy;
    };

    var _reply = function(mail) {
        var copy = angular.copy(mail);
        delete copy.id;

        copy.to = [copy.sender];
        copy.subject = "Re: " + copy.subject;

        copy.body.html = citeService.citeAsHtml(copy);
        copy.body.plain = citeService.citeAsPlain(copy);

        delete copy.sender;

        return copy;
    };

    var _replyToAll = function(mail) {
        var copy = angular.copy(mail);

        copy.to = [copy.sender];
        delete copy.sender;
        copy.subject = "Re: " + copy.subject;

        return copy;
    };

    return {
        createForward: _createForward,
        reply: _reply,
        replyToAll: _replyToAll
    };

}]);

app.service('citeService',['i18nService', function(i18nService) {

    var _split = function (text) {
        var split = text.split("\r\n");
        if(split.length === 1) {
            return text.split("\n");
        }
        return split;
    };

    var _citeAsPlain = function(mail) {
        var cited = "\r\n\r\nOn " + mail.date +" "+ mail.sender.email + " wrote: ";

        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            cited = cited + "> " + val + "\r\n";
        });

        return cited;
    };

    var _citeAsHtml = function(mail) {

        var cited = '<br><br><div class="moz-cite-prefix">On ' + mail.date +' '+ mail.sender.email + ' wrote:<br></div>';

        cited = cited + '<blockquote type="cite" cite="mid:TODO">';

        //we do not cite html bodies
        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            cited = cited + val + "<br>";
        });

        return cited + "</blockquote>";
    };

    return {
        citeAsPlain: _citeAsPlain,
        citeAsHtml: _citeAsHtml
    }

}]);

app.service('draftService',['$q', '$http', 'API_HOME', function($q, $http, API_HOME) {

    var _save = function(mail) {
        var deferred = $q.defer();
        var id = "";
        var method = "POST";

        if(angular.isDefined(mail) && angular.isDefined(mail.id)) {
            id = "/" + mail.id;
            method = "PUT";
        }

        //TODO reply-to
        mail.date = new Date();

        $http({method: method, url: API_HOME +'message/draft'+id, data: mail})
            .success(function(result) {
                deferred.resolve(result.id);
            })
            .error(function(data) {
                deferred.reject(data);
            });

        return deferred.promise;
    };

    var _isDraft = function(mail) {
        //TODO
        return mail.folder.startsWith("INBOX/Drafts");
    };

    return {
        save: _save,
        isDraft: _isDraft
    };

}]);

app.service('attachmentService',['$q', '$http', 'API_HOME', function($q, $http, API_HOME) {

    var _save = function(attachment) {
        var deferred = $q.defer();
        var id = "";
        var method = "POST";

        $http({
            method: method,
            url: API_HOME +'attachment/'+attachment.messageId,
            headers: {
                'Content-Type': undefined
            },
            data: attachment.data,
            transformRequest: function (data) {
                return data;
            }
        })
        .success(function(result) {
            deferred.resolve(result.id);
        })
        .error(function(data) {
            deferred.reject(data);
        });

        return deferred.promise;
    };

    var _delete = function(id) {
        var deferred = $q.defer();

        $http({
            method: "DELETE",
            url: API_HOME +'attachment/'+id
        })
        .success(function(result) {
            deferred.resolve(result.id);
        })
        .error(function(data) {
            deferred.reject(data);
        });

        return deferred.promise;
    };

    return {
        save: _save,
        delete: _delete
    };

}]);