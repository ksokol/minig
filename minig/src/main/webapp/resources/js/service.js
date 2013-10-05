
app.factory('urlTemplateService', function(API_HOME) { 
	
	return {
		
		templateFor: function(endpoint) {
			var url = API_HOME + encodeURIComponent(endpoint) + '?';
				
			return  {
				params: function(params) {
					for(key in params) {
						url += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&';
					}
					
					return this;
				},
				
				param: function(key, value) {
					url += encodeURIComponent(key) + '=' + encodeURIComponent(value) + '&';					
					return this;
				},
				
				build: function() {
					return url;
				}
			}
		}
	
	}
});

app.factory('mailService', function($http, urlTemplateService) {
	
	var defaults = {page: 1, page_length: 10};
	
	return {
		
		getFolder: function() {
			var url = urlTemplateService.templateFor('folder').build();
			return $http.get(url);
		},
	
		getMailbox: function(options) {
		    var o = angular.extend({}, defaults, options);
		    var url = urlTemplateService.templateFor('message').params(o).build();
		    return $http.get(url);
		}
	
	}
});