
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

app.directive("notification", function(i18nService) {
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

            scope.$on("folder-deleted", function() {
                show(i18nService.resolve("Folder removed"));
            });

            scope.$on("folder-created", function() {
                show(i18nService.resolve("Folder created"));
            });
        }
    };
});

app.directive("inlineFolderSelect", function($http, $document, $window, $compile, $rootScope, i18nService) {
	var body = angular.element($document[0].body);
	var cached;

    $http.get("inline-folder-select.html")
    .success(function(html) {
        cached = html;
    });

    var build = function($scope, parent, attrs) {
        var compiled = $compile(cached);
        var elem = angular.element(compiled($scope));
        body.append(elem);

        var overlay = angular.element('.bg-overlay');

        overlay.on('click', function() {
            elem.remove();
        });

        $rootScope.$on('folder-intent-done', function(e) {
            elem.remove();
        });

        elem.on('$destroy', function() {
            elem.unbind('click');
        });

        overlay.css('height', $window.innerHeight);
        overlay.css('width', $window.innerWidth);

        $scope.$apply(attrs.inlineFolderSelect);
    };

	return {
	    restrict : "A",
		link: function($scope, element, attrs) {
            element.bind("click", function() {
                if (!$scope.hasMailSelected()) {
                    return;
                }

                if(cached) {
                    build($scope, element, attrs);
                } else {
                    $rootScope.$broadcast('error', i18nService.resolve("Template not found. Cannot proceed."));
                }
			});
		}
	}
});

app.directive("moreActions", function($window, $rootScope) {
    return {
        restrict : "A",
		link: function($scope, element, attrs) {
            var overlay = angular.element('.bg-overlay-ma');
            var ma = angular.element('#more-actions');

            overlay.on('click', function() {
                ma.hide();
                overlay.hide();
                return false;
            });

            ma.find('a').on('click', function() {
                var event = angular.element(this).attr("data-event");
                $rootScope.$broadcast(event);
                return false;
            });

	        element.bind("click", function() {
                if (!$scope.hasMailSelected()) {
                    return;
                }

                overlay.css('height', $window.innerHeight);
                overlay.css('width', $window.innerWidth);
                ma.show();
                overlay.show();
			});
		}
    };
});

app.directive("pagination", function(DEFAULT_PAGE_SIZE, $http, $compile, pagerFactory, templateService) {

    return {
        restrict: "E",
        controller: function($scope) {

            $scope.pager = {
                pages: 1,
                currentPage: 1,
                length: DEFAULT_PAGE_SIZE,
                fullLength: 0
            };

            $scope.isFirstPage = function() {
                return $scope.pager.currentPage === 1;
            }

            $scope.isLastPage = function() {
                return $scope.pager.currentPage === $scope.pager.pages;
            }

            $scope.firstPage = function() {
                $scope.pager.currentPage = 1;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            }

            $scope.lastPage = function() {
                $scope.pager.currentPage = $scope.pager.pages;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            }

            $scope.nextPage = function() {
                $scope.pager.currentPage = $scope.pager.currentPage+1;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            }

            $scope.previousPage = function() {
                $scope.pager.currentPage = $scope.pager.currentPage-1;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            }
        },
        link: function($scope, element, attrs) {
            $scope.$watch('data', function(data) {
                if(data) {
                    templateService.template("pagination.html")
                    .then(function(html) {
                         $scope.pager = pagerFactory.newInstance(data.page, data.pageLength, data.fullLength);

                         var compiled = $compile(html);
                         var elem = angular.element(compiled($scope));

                         element.html(elem);
                    });
                }
            });
        }
    }
});