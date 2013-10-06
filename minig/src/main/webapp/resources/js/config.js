var app = angular.module("minigApp", ['ngResource'])
.constant('API_HOME', 'api/1/')
.constant('INITIAL_MAILBOX', 'INBOX'); //TODO: INBOX shouldn't be hardcoded

app.config(function($httpProvider) {

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

});

app.config(function() {
	
	return {
		initialMailbox : "INBOX"
	}
	
});