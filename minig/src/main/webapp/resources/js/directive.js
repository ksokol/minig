
app.directive("loadingIndicator", function() {
    return {
        restrict : "A",
        link : function(scope, element, attrs) {
            scope.$on("loading-started", function(e) {
                element.css({"display" : ""});
            });

            scope.$on("loading-complete", function(e) {
                element.css({"display" : "none"});
            });

            scope.$on("error", function(e) {
                element.css({"display" : "none"});
            });
        }
    };
});

app.directive("notification", function() {
	var id = null;

    return {
        restrict : "A",
        link : function(scope, element, attrs) {
        	
        	function show(message) {
        		 clearTimeout(id);
        		 var div = element.find('div');
                 div.text(message);
             	 element.find('table').css({"display" : ""});
             	 hide();
        	}
        	
        	function hide() {
            	id = setTimeout(function() {
            		element.find('table').css({"display" : "none"});
            		var div = element.find('div');
            		div.text("");
            		element.find('table').removeClass('notification-error');
            	}, 3000);
        	}
        	
            scope.$on("notification", function(e, message) {
            	show(message);            	
            });
            
            scope.$on("error", function(e, message) {            	
            	element.find('table').addClass('notification-error');
            	show(message);
            });
        }
    };
});

app.directive("inlineFolderSelect", function($http, $document, $window, $compile, MailResource) {

	var body = angular.element($document[0].body);

	
	return {
		
		link: function($scope, element, attrs) {

			 element.bind("click", function() {
				
				$http.get("inline-folder-select.html")
				.success(function(html) {
					var compiled = $compile(html);
					
					var elem = compiled($scope);
					var overlay = angular.element(elem);

					overlay.bind('click', function() {
						overlay.remove();
					});

					body.append(elem);
					
					overlay.css('height', $window.innerHeight);
					overlay.css('width', $window.innerWidth);
					
					
				});
				
				
			 });
			 
			 
			 
		}
		
	}
	
});