var app = angular.module("minigApp", ['ngResource', 'ngRoute', 'LocalStorageModule', 'ngSanitize', 'minigTextAngular'])
.constant('API_HOME', 'api/1/')
.constant('DEFAULT_PAGE_SIZE', 20)
.constant('MAIL_CACHE_SIZE', 5)
.constant('INITIAL_MAILBOX', 'INBOX'); //TODO: INBOX shouldn't be hardcoded

app.config(function($httpProvider, $routeProvider) {
	
    $httpProvider.interceptors.push(function($q, $window, $rootScope, i18nService) {
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
    });
    
    $httpProvider.interceptors.push(function($q) {
        return {
            'request': function(config) {            	
            	config.headers["X-Requested-With"] = "XMLHttpRequest";
                return config || $q.when(config);
            }
        };
    });
    
    $httpProvider.interceptors.push(function($q, $rootScope) {
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
    });

    $routeProvider
    .when('/box', {
        templateUrl: "box.html",
        controller: 'MailOverviewCtrl',
        reloadOnSearch: false
    })
    .when('/folder', {
        templateUrl: "folder.html",
        controller: 'FolderSettingsCtrl'
    })
    .when('/composer', {
        templateUrl: "composer.html",
        controller: 'ComposerCtrl',
        reloadOnSearch: false
    })
    .when('/message', {
        templateUrl: "message.html",
        controller: 'MessageCtrl'
    })
    .otherwise({redirectTo: '/box'});

});

angular.module('minigTextAngular', ['textAngular'])
.config(['$provide', function($provide) {
    $provide.decorator('taOptions', ['taRegisterTool', '$delegate', function(taRegisterTool, taOptions) {

        taRegisterTool('strikeThrough', {
            iconclass: "fa fa-strikeThrough",
            action: function() {
                this.$editor().wrapSelection('strikeThrough',null);
            }
            ,activeState: function() {
                return document.queryCommandState('strikeThrough');
            }
        });

        taRegisterTool('indent', {
            iconclass: 'fa fa-indent',
            action: function(){
                return this.$editor().wrapSelection("indent", null);
            },
            activeState: function(){
                return this.$editor().queryFormatBlockState('blockquote');
            }
        });
        taRegisterTool('outdent', {
            iconclass: 'fa fa-outdent',
            action: function(){
                return this.$editor().wrapSelection("outdent", null);
            },
            activeState: function(){
                return false;
            }
        });

        taOptions.toolbar = [
            ['bold', 'italics', 'underline'],
            ['justifyLeft','justifyCenter','justifyRight'],
            [/* 'strikeThrough' */ 'indent','outdent', 'ul', 'ol']
        ];
        return taOptions;
    }]);

}]);
