
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

    var DEFAULT =  {currentPage: 0, pageLength: DEFAULT_PAGE_SIZE, fullLength: 0, pages: 0, start: 0, end: 0};

    return {
        newInstance: function(currentPage, pageLength, fullLength) {
            if( !angular.isNumber(fullLength) ||
                !angular.isNumber(currentPage) ||
                pageLength === "undefined" ||
                (fullLength) < 1 ||
                (pageLength) < 1 ||
                currentPage < 0
            ) {
                return angular.copy(DEFAULT);
            }

            var pagination = {
                currentPage: currentPage,
                fullLength: fullLength,
                pageLength: pageLength || DEFAULT_PAGE_SIZE,
                start: 0,
                end: 0
            };

            pagination.pages = Math.ceil((pagination.fullLength) / pagination.pageLength) -1 ;

            if(currentPage <= pagination.pages) {
                pagination.start = (pagination.currentPage === 0) ? 1 : (pagination.currentPage * pagination.pageLength) + 1;
                pagination.end = Math.min((pagination.currentPage + 1) * pagination.pageLength, pagination.fullLength);
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
        for(i=0;i<copy.length;i++) {
            if(copy[i].id === data.id) {
                copy[i] = data;
                cache = copy;
                return;
            }
        }
    }
});

app.service('routeService', ['$rootScope', '$route', '$location', '$log', 'localStorageService', function($rootScope, $route, $location, $log, localStorageService) {

    $rootScope.$on('$routeChangeError', function(event, next, current) {
        $log.error(event)
    });

    $rootScope.$on('$routeChangeStart', function(event, next, current) {
        if(current) {
            localStorageService.set('lastRoute', {'path' : current.originalPath, 'params': current.params });
        }
        localStorageService.set('currentRoute', {'path' : next.originalPath, 'params': next.params });
    });

    var _navigate = function(route, reload) {
        $log.info("navigate to ", route, "reload: ", reload);
        $location.url(route);
        reload && $route.reload();
    };

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
        var reload = route.reload === true;

        if(!route.params || Object.keys(route.params).length == 0) {
            return _navigate("/"+route.path, reload);
        }

        var url = "/"+route.path+"?";
        for(k in route.params) {
            url = url + k + "=" + encodeURIComponent(route.params[k]) + "&";
        }

        _navigate(url, reload);
    };

    return {
        navigateToPrevious : navigateToPrevious,
        previous: previous,
        currentRoute : currentRoute,
        navigateTo : navigateTo
    };
}]);

app.service('submissionService',['$q', '$http', 'API_HOME', function($q, $http, API_HOME) {

    var _disposition = function(mail) {
        var deferred = $q.defer();

        if(!angular.isDefined(mail) && !angular.isDefined(mail.id)) {
            deferred.reject("mail id undefined");
            return;
        }

        mail.date = new Date();

        $http({method: 'POST', url: API_HOME +'/submission/disposition/'+mail.id})
        .then(function() {
            deferred.resolve();
        }, function(data) {
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

        $http({method: 'POST', url: API_HOME +'submission', data: mail})
            .then(function() {
                deferred.resolve();
            }, function(data) {
                deferred.reject(data);
            });

        return deferred.promise;
    };

    return {
        disposition: _disposition,
        submission: _submission
    };

}]);

app.service('composerService',['citeService','userService', function(citeService, userService) {

    var _createForward = function(mail) {
        var copy = angular.copy(mail);
        delete copy.id;

        //TODO localize
        copy.subject = "[Fwd: " + copy.subject +"]";

        copy.body.html = citeService.forwardAsHtml(copy);
        copy.body.plain = citeService.forwardAsPlain(copy);
        copy.forwardedMessageId = mail.messageId;

        delete copy.to;
        delete copy.sender;
        delete copy.inReplyTo;

        return copy;
    };

    var _reply = function(mail) {
        var copy = angular.copy(mail);
        delete copy.id;

        copy.to = [copy.sender];

        //TODO localize
        copy.subject = "Re: " + copy.subject;

        copy.body.html = citeService.citeAsHtml(copy);
        copy.body.plain = citeService.citeAsPlain(copy);
        copy.inReplyTo = mail.messageId;

        delete copy.sender;
        delete copy.cc;
        delete copy.bcc;
        delete copy.forwardedMessageId;

        return copy;
    };

    var _replyToAll = function(mail) {
        var copy = angular.copy(mail);
        delete copy.id;

        var tmp = [];

        for(i=0;i<copy.to.length;i++) {
            tmp[copy.to[i].email] = copy.to[i];
        }

        tmp[copy.sender.email] = copy.sender;
        delete tmp[userService.getCurrentEmail()];

        var recipients = [];
        for(k in tmp) {
            recipients.push(tmp[k]);
        }

        copy.to = recipients;

        //TODO localize
        copy.subject = "Re: " + copy.subject;
        copy.body.html = citeService.citeAsHtml(copy);
        copy.body.plain = citeService.citeAsPlain(copy);
        copy.inReplyTo = mail.messageId;

        delete copy.sender;
        delete copy.cc;
        delete copy.bcc;

        return copy;
    };

    return {
        createForward: _createForward,
        reply: _reply,
        replyToAll: _replyToAll
    };

}]);

app.service('citeService',['$interpolate', 'i18nService', function($interpolate) {

    var _split = function (text) {
        var split = text.split("\r\n");
        if(split.length === 1) {
            return text.split("\n");
        }
        return split;
    };

    var _citeAsPlain = function(mail) {
        var tmpl = "\r\n\r\n{{'On' | i18n}} {{mail.date}} {{mail.sender | displayName}} {{'wrote' | i18n}}: \r\n{{body}}";

        var cited="";
        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            cited = cited + "> " + val + "\r\n";
        });

        var exp = $interpolate(tmpl);
        return exp({mail: mail, body: cited});
    };

    var _citeAsHtml = function(mail) {
        var tmpl = '<br><br>'+
                   '<div class="moz-cite-prefix">'+
                   "{{'On' | i18n}} {{mail.date}} {{mail.sender | displayName}} {{'wrote' | i18n}}:<br>"+
                   '</div><blockquote type="cite" cite="mid:{{mail.messageId}}">{{body}}</blockquote>';

        //we do not cite html bodies
        var cited="";
        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            cited = cited + he.encode(val) + "<br>";
        });

        var exp = $interpolate(tmpl);
        return exp({mail: mail, body: cited});
    };

    var _forwardAsPlain = function(mail) {
        var tmpl = "<br><br>-------- {{'Original Message' | i18n}} --------\r\n" +
                      "{{'Subject' | i18n}}: {{mail.subject}}\r\n" +
                      "{{'Date' | i18n}}: {{mail.date}}\r\n" +
                      "{{'From' | i18n}}: {{mail.sender.email| displayName}}\r\n" +
                      "{{'To' | i18n}}: {{mail.to | displayName}}\r\n\r\n" +
                      "{{body}}";

        var forward="";
        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            forward = forward + val + "\r\n";
        });

        var exp = $interpolate(tmpl);
        return exp({mail: mail, body: forward});
    };

    var _forwardAsHtml = function(mail) {
        var tmpl = "<br><br>-------- {{'Original Message' | i18n}} --------<br>" +
                   "<strong>{{'Subject' | i18n}}</strong>: {{mail.subject}}<br>" +
                   "<strong>{{'Date' | i18n}}</strong>: {{mail.date}}<br>" +
                   "<strong>{{'From' | i18n}}</strong>: {{mail.sender.email | displayName}}<br>" +
                   "<strong>{{'To' | i18n}}</strong>: {{mail.to | displayName}}<br><br>" +
                   "{{body}}";

        //we do not cite html bodies
        var forward = "";
        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            forward = forward + he.encode(val) + "<br>";
        });

        var exp = $interpolate(tmpl);
        return exp({mail: mail, body: forward});
    };

    return {
        citeAsPlain: _citeAsPlain,
        citeAsHtml: _citeAsHtml,
        forwardAsPlain: _forwardAsPlain,
        forwardAsHtml: _forwardAsHtml
    }

}]);

app.service('htmlConversion', function() {

    var _convert = function(html) {
        return md(he.decode(html || ''));
    };

    return {
        convertToPlain: _convert
    }
});

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
            .then(function(result) {
                deferred.resolve(result.data);
            }, function(data) {
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
        .then(function(result) {
            deferred.resolve(result.data);
        }, function(data) {
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
        .then(function(result) {
            deferred.resolve(result.data);
        }, function(data) {
            deferred.reject(data);
        });

        return deferred.promise;
    };

    return {
        save: _save,
        delete: _delete
    };

}]);

app.service('deferService', ['$q', function($q) {

    var _deferred = function(fn, params) {
        var deferred = $q.defer();
        fn(params).$promise
        .then(function(result) {
            deferred.resolve(result);
        })
        .catch(function(error) {
            deferred.reject(error);
        });
        return deferred.promise;
    };

    var _resolved = function(params) {
        var deferred = $q.defer();
        deferred.resolve(params);
        return deferred.promise;
    };

    return {
        deferred: _deferred,
        resolved: _resolved
    }
}]);
