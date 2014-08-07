
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
                ma.hide();
                overlay.hide();
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

app.directive("pagination", function(DEFAULT_PAGE_SIZE, pagerFactory) {

    return {
        restrict: "E",
        templateUrl: "pagination.html",
        controller: function($scope) {
            $scope.pager = {
                pages: 1,
                currentPage: 1,
                length: DEFAULT_PAGE_SIZE,
                fullLength: 0
            };

            $scope.isFirstPage = function() {
                return $scope.pager.currentPage === 1;
            };

            $scope.isLastPage = function() {
                return $scope.pager.currentPage === $scope.pager.pages;
            };

            $scope.firstPage = function() {
                $scope.pager.currentPage = 1;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            };

            $scope.lastPage = function() {
                $scope.pager.currentPage = $scope.pager.pages;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            };

            $scope.nextPage = function() {
                $scope.pager.currentPage = $scope.pager.currentPage+1;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            };

            $scope.previousPage = function() {
                $scope.pager.currentPage = $scope.pager.currentPage-1;
                $scope.currentPage = $scope.pager.currentPage;
                $scope.updateOverview();
            };

            $scope.showPager = function() {
                return $scope.pager.start !== undefined;
            };

            $scope.$watch('data', function(data) {
                if(!data) {
                    return;
                }
                $scope.pager = pagerFactory.newInstance(data.page, data.pageLength, data.fullLength);
            });
        }
    }
});

app.directive('backLink', ['$location', 'routeService', function($location, routeService) {
    return {
        restrict: 'E',
        template: '<a class="gwt-Anchor noWrap back" href="#{{url}}">{{"« Back" | i18n }}</a>',
        link: function (scope, elem, attrs) {
            scope.url = routeService.previous();

            if(routeService.currentRoute('message')) {
                elem.show();
            } else {
                elem.hide();
            }
        }
    };
}]);

app.directive("mainActions", function($rootScope, routeService, MailResource) {

    var _folderIntentDone = function _folderIntentDone(folderAction) {
        $rootScope.$broadcast('folder-intent-done', folderAction);
    };

    return {
        restrict: "E",
        templateUrl: 'main_actions.jsp',
        link: function($scope, element, attrs) {

            $scope.moveToFolder = function() {
                $scope.folderIntent = "move";
            };

            $scope.copyToFolder = function() {
                $scope.folderIntent = "copy";
            };

            $scope.$on('folder-intent', function(e, folder) {
                var params = {folder: folder, mails: $scope.getSelectedMails()};

                switch($scope.folderIntent)    {
                    case "copy": MailResource.copy(params).$promise.then(_folderIntentDone("copy")); break;
                    case "move": MailResource.move(params).$promise.then(_folderIntentDone("move")); break;
                }
            });
        }
    }
});

app.directive("selectOptions", function($rootScope, routeService, MailResource) {

    return {
        restrict: "A",
        link: function($scope, element, attrs) {
            if(routeService.currentRoute('message')) {
                element.hide();
            } else {
                element.show();
            }

            var _selectAll = function(flag) {
                angular.forEach($scope.getMails(), function(mail) {
                    mail.selected = flag;
                });
            };

            $scope.selectNone = function() {
                _selectAll(false);
            };

            $scope.selectAll = function() {
                _selectAll(true);
            };

        }
    }
});

app.directive("senderPanel", function() {

    return {
        restrict: "E",
        replace : true,
        controller :  [ "$scope", function ($scope) {
            $scope.formatSender = function(sender) {
                if(sender === undefined) {
                    return "undisclosed sender";
                }
                return sender.email;
            }
        }],
        template: '<span class="bold noWrap recipientLabel1">{{formatSender(mail.sender)}}</span>'
    }
});

app.directive("recipientPanel", function() {

    return {
        restrict: "E",
        replace : true,
        controller :  [ "$scope", function ($scope) {
            $scope.formatRecipient = function(recipients) {
                var formatted = "";
                if(recipients === undefined) {
                    return "undisclosed recipients";
                }
                angular.forEach(recipients, function(recipient) {
                    formatted = formatted + recipient.email + ", ";
                });
                return formatted.substring(0, formatted.length - 2);
            }
        }],
        template: '<span class="noWrap recipientLabel2">{{formatRecipient(mail.to)}}</span>'
    }
});

app.directive("conversationDisplay", function() {

    return {
        restrict: "C",
        controller :  [ "$scope", function ($scope) {
            $scope.hideDetails = true;

            $scope.toggleDetails = function() {
                $scope.hideDetails = !$scope.hideDetails;
            }
        }]
    }
});

app.directive("messageText", function() {

    var formatPlain = function (text) {
        return text.split("\r\n");
    };

    var formatHtml = function (html) {
        return html.split(/<\s*b\s*r\s*\/?>/g);
    };

    return {
        restrict: "E",
        link: function ($scope, element, attrs) {
            $scope.$watch('mail', function(mail) {
                if(!mail || !mail.body) {
                    return;
                }
                //TODO support html mails
                if(mail.body.plain) {
                    $scope.messageText = formatPlain(mail.body.plain);
                } else {
                    $scope.messageText = formatHtml(mail.body.html);
                }
            });
        }
    }
});

app.directive("dispositionNotificationPanel", function($rootScope, submissionService, i18nService) {

    return {
        restrict: "C",
        controller :  [ "$scope", function ($scope) {
            $scope.showDispositionNotification = false;

            $scope.declineDisposition = function() {
                $scope.showDispositionNotification = false;
            };

            $scope.acceptDisposition = function() {
                submissionService.disposition($scope.mail)
                .then(function() {
                    $rootScope.$broadcast('notification', i18nService.resolve("Disposition sent"));
                    $scope.showDispositionNotification = false;
                })
                .catch(function(error) {
                    $rootScope.$broadcast('error', i18nService.resolve(error));
                });
            };

            $scope.$watch('mail', function(mail) {
                if (!mail) {
                    return;
                }
                $scope.showDispositionNotification = $scope.mail.dispositionNotification && $scope.mail.dispositionNotification.length > 0
            });
        }]
    }

});


app.directive("attachmentPanel", function($rootScope, i18nService, AttachmentResource) {

    return {
        restrict: "E",
        replace: true,
        scope: {
            id: "@"
        },
        templateUrl:'attachment.jsp',
        controller :  [ "$scope", function ($scope) {

            $scope.load = function(id) {
                if(!id) {
                    return;
                }
                AttachmentResource.findById(id)
                .then(function(attachments) {
                    $scope.attachments = attachments.attachmentMetadata;
                });
           };
        }]        ,
        link: function ($scope) {
            $scope.$watch('id', $scope.load);
        }
    }

});

app.directive("recipientInput", function() {

    var pattern = /(.+)@(.+){2,}\.(.+){2,}/;

    //32 is blank
    //225 is @
    //13 is enter
    var keyCodes = [13,32,225];

    return {
        restrict: "E",
        replace : true,
        template:'<div class="wrap">' +
                 '   <div>' +
                 '       <table cellspacing="0" cellpadding="0" title="" class="recipient" ng-repeat="recipient in recipients">' +
                 '           <tbody>' +
                 '               <tr>' +
                 '                 <td align="left" style="vertical-align: middle;">' +
                 '                     <div class="gwt-Label">{{recipient.email}}</div>' +
                 '                 </td>' +
                 '                 <td align="left" style="vertical-align: middle;">' +
                 '                     <img src="resources/images/x.gif" class="deleteRecip" title="Remove" ng-click="remove(recipient)">' +
                 '                  </td>' +
                 '               </tr>' +
                 '            </tbody>' +
                 '        </table>' +
                 '      </div>' +
                 '   <input type="text" class="gwt-TextBox" ng-model="recipient" ng-keyup="change($event)" ng-blur="blur($event)">' +
                 '</div>',
        scope: {
            recipients : "="
        },
        controller :  [ "$scope", function ($scope) {

            var add = function(recipient) {
                var found = false;
                angular.forEach($scope.recipients,function(val) {
                    if(val.email === recipient) {
                        found = true;
                    }
                });
                $scope.recipient = null;
                if(found) {
                    return;
                }
                $scope.recipients.push({displayName: recipient, email: recipient, display: recipient});

            };

            $scope.remove = function(recipient) {
                var indexOf = $scope.recipients.indexOf(recipient);
                indexOf !== -1 && $scope.recipients.splice(indexOf,1);
            };

            $scope.change = function(event) {
                keyCodes.indexOf(event.keyCode) !== -1 && pattern.test($scope.recipient) && add($scope.recipient);
            };

            $scope.blur = function(event) {
                pattern.test($scope.recipient) && add($scope.recipient);
                $scope.recipient = null;
            };
        }]
    }
});

app.directive("attachmentUpload", function() {

    return {
        restrict: "E",
        replace: true,
        scope: {
            mail: "=",
            after: "&"
        },
        templateUrl: 'attachment-upload.jsp',
        controller: [ "$scope", "attachmentService", function ($scope, attachmentService) {
            // http://stackoverflow.com/questions/17922557/angularjs-how-to-check-for-changes-in-file-input-fields
            $scope.blur = function(element) {
                var file = element.files[0];
                var formData = new FormData();
                formData.append(file.name, file);
                attachmentService.save({messageId: $scope.mail.id, data: formData})
                .then(function(result) {
                    $scope.mail.id = result;
                });
            }
        }]
    }

});
