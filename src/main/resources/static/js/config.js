var app = angular.module("minigApp", ['ngResource', 'ngRoute', 'LocalStorageModule', 'ngSanitize', 'minigTextAngular'])
.constant('API_HOME', 'api/1/')
.constant('DEFAULT_PAGE_SIZE', 20)
.constant('MAIL_CACHE_SIZE', 5)
.constant('INITIAL_MAILBOX', 'INBOX'); //TODO: INBOX shouldn't be hardcoded

app.config(['$httpProvider', '$routeProvider', function($httpProvider, $routeProvider) {

    $httpProvider.interceptors.push(['$q', '$window', '$rootScope', 'i18nService', function($q, $window, $rootScope, i18nService) {
        return {
            'responseError': function(rejection) {
            	if(rejection.status === 401) {
            		$window.location.reload();
                }
            	if(rejection.status >= 500) {
            		if(typeof rejection.data === 'string') {
            			$rootScope.$broadcast('error', i18nService.resolve(rejection.data));
            		} else {
            			$rootScope.$broadcast('error', i18nService.resolve(rejection.data.message));
            		}
            	}

        		return $q.reject(rejection);
            }
        };
    }]);

    $httpProvider.interceptors.push(['$q', function($q) {
        return {
            'request': function(config) {
            	config.headers["X-Requested-With"] = "XMLHttpRequest";
                return config || $q.when(config);
            }
        };
    }]);

    $httpProvider.interceptors.push(['$q', '$rootScope', function($q, $rootScope) {
        return {
            'request': function(config) {
                $rootScope.$broadcast('loading-started');
                return config || $q.when(config);
            },
            'response': function(response) {
                $rootScope.$broadcast('loading-complete');
                return response || $q.when(response);
            }
        };
    }]);

    $routeProvider
    .when('/box', {
        templateUrl: "templates/box.html",
        controller: 'MailOverviewCtrl',
        reloadOnSearch: false
    })
    .when('/folder', {
        templateUrl: "templates/folder.html",
        controller: 'FolderSettingsCtrl'
    })
    .when('/composer', {
        templateUrl: "templates/composer.html",
        controller: 'ComposerCtrl',
        reloadOnSearch: false
    })
    .when('/message', {
        templateUrl: "templates/message.html",
        controller: 'MessageCtrl'
    })
    .otherwise({redirectTo: '/box'});

}]);

angular.module('minigTextAngular', ['textAngular'])
.config(['$provide', function($provide) {
    $provide.decorator('taOptions', ['taRegisterTool', '$delegate', function(taRegisterTool, taOptions) {
        taOptions.toolbar = [
            ['bold', 'italics', 'underline'],
            ['justifyLeft','justifyCenter','justifyRight'],
            [ 'indent','outdent', 'ul', 'ol']
        ];
        return taOptions;
    }]);

}]);

app.factory('userService', ['$http', 'API_HOME', function($http, API_HOME) {
    var currentEmail;

    $http.get(API_HOME + "me")
    .then(function(result) {
        currentEmail = result.data.username;
    });

    return {
        getCurrentEmail: function() {
            return currentEmail;
        }
    };

}]);
