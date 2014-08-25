
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

app.service('mailCache', function($log, MAIL_CACHE_SIZE) {
    var cache = [];

    var _remove = function(id) {
        var copy = angular.copy(cache);
        for(i=0;i<copy.length;i++) {
            if(copy[i].id === id) {
                copy.splice(i, 1);
                cache = copy;
                return;
            }
        }
    };

    var _add = function(mail) {
        _remove(mail.id);
        var copy = angular.copy(cache);

        if(copy.length == MAIL_CACHE_SIZE) {
            $log.info("hit max. cache size of ", MAIL_CACHE_SIZE, " elements. recycling last.");
            copy = copy.slice(0, copy.length -1);
        }

        $log.info("caching ", mail.id);
        copy.push({id: mail.id, data: mail});
        cache = copy;
        $log.info("cache ", cache.length, "/", MAIL_CACHE_SIZE);
    };

    var _get = function(id) {
        $log.info("searching cache for ", id);
        var copy = angular.copy(cache);
        for(i=0;i<copy.length;i++) {
            $log.info("comparing keys ", copy[i].id, " with ", id);
            if(copy[i].id === id) {
                $log.info("cache hit ", id);
                return copy[i].data;
            }
        }
        $log.info("cache miss for ", id);
    };

    return {
        clear : function() {
            cache = [];
        },
        evict : function(id) {
            _remove(id);
        },
        add : function(data) {
            _add(data);
        },
        get : function(id) {
            return _get(id);
        },
        size : function() {
            return cache.length;
        }
    };


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

        $http({method: 'POST', url: API_HOME +'submission', data: mail})
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

app.service('composerService',['citeService','userService', function(citeService, userService) {

    var _createForward = function(mail) {
        var copy = angular.copy(mail);
        delete copy.id;

        //TODO localize
        copy.subject = "[Fwd: " + copy.subject +"]";

        copy.body.html = citeService.forwardAsHtml(copy);
        copy.body.plain = citeService.forwardAsPlain(copy);

        delete copy.to;
        delete copy.sender;

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

app.service('citeService',['$interpolate', 'i18nService', function($interpolate, i18nService) {

    var _split = function (text) {
        var split = text.split("\r\n");
        if(split.length === 1) {
            return text.split("\n");
        }
        return split;
    };

    var _citeAsPlain = function(mail) {
        //TODO localize
        var cited = "\r\n\r\nOn " + mail.date +" "+ mail.sender.email + " wrote: ";

        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            cited = cited + "> " + val + "\r\n";
        });

        return cited;
    };

    var _citeAsHtml = function(mail) {
        //TODO localize
        var cited = '<br><br><div class="moz-cite-prefix">On ' + mail.date +' '+ mail.sender.email + ' wrote:<br></div>';
        cited = cited + '<blockquote type="cite" cite="mid:TODO">';

        //we do not cite html bodies
        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            cited = cited + he.encode(val) + "<br>";
        });

        return cited + "</blockquote>";
    };

    var _forwardAsPlain = function(mail) {
        //TODO localize
        var forward = "\r\n\r\n-------- Original Message --------\r\n";
        forward = forward + "Subject: " + mail.subject + "\r\n";
        forward = forward + "Date: " + mail.date + "\r\n";
        forward = forward + "From: " + "\r\n";
        forward = forward + "To: " + "\r\n\r\n";

        var split = _split(mail.body.plain);
        angular.forEach(split, function(val) {
            forward = forward + val + "\r\n";
        });

        return forward;
    };

    var _forwardAsHtml = function(mail) {
        var tmpl = "<br><br>-------- {{'Original Message' | i18n}} --------<br>" +
                   "<strong>{{'Subject' | i18n}}</strong>: {{mail.subject}}<br>" +
                   "<strong>{{'Date' | i18n}}</strong>: {{mail.date}}<br>" +
                   "<strong>{{'From' | i18n}}</strong>: {{mail.sender.email}}<br>" +
                   "<strong>{{'To' | i18n}}</strong>: {{mail.to}}<br><br>" +
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

app.service('draftService',['$q', '$http', 'mailCache', 'API_HOME', function($q, $http, mailCache, API_HOME) {

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
                mailCache.add(result);
                deferred.resolve(result);
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
            deferred.resolve(result);
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
            deferred.resolve(result);
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

app.service('userService', function() {
    var currentEmail;

    return {
        setCurrentEmail: function(email) {
            currentEmail = email;
        },
        getCurrentEmail: function() {
            return currentEmail;
        }
    }
});