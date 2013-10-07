
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