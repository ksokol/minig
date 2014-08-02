
app.controller('FolderListCtrl', function($scope, $rootScope, FolderResource) {
    $scope.folders = [];

    FolderResource.findAll().then(function (folders) {
        $scope.folders = folders;
    });

    $rootScope.$on('folder-created', function() {
        $scope.refresh();
    });

    $rootScope.$on('folder-deleted', function() {
        $scope.refresh();
    });

    $rootScope.$on('folder-updated', function() {
        $scope.refresh();
    });

	$scope.reset = function() {
		$scope.query = null;
	};

    $scope.onFolderSelect = function(selectedFolder) {
       $rootScope.$broadcast('folder-intent', selectedFolder);
    };

    $scope.refresh = function() {
        FolderResource.findAll().then(function(folders) {
            $scope.folders = folders;
        });
    };
})
.controller('MailOverviewCtrl', function($scope, $rootScope, $routeParams, $location, MailResource, i18nService, INITIAL_MAILBOX) {

	$scope.currentFolder = ($routeParams.id) ? $routeParams.id : INITIAL_MAILBOX;
	$scope.currentPage = 1;
	$scope.selected = [];
	$scope.folderIntent;
	$scope.data;

	$scope.getMails = function() {
	    return ($scope.data && $scope.data.mailList) ? $scope.data.mailList : [];
	};

    function _updateFlags(fnDecide) {
        var tmp = [];

        angular.forEach($scope.getSelectedMails(), function(mail) {
            if(fnDecide(mail)) {
                tmp.push(mail);
            }

            mail.selected = false;
        });

        if(tmp.length > 0) {
           MailResource.updateFlags(tmp).$promise
           .catch(function() {
                $scope.updateOverview();
           });
        }

        $rootScope.$broadcast("more-actions-done");
    }

    $scope.getSelectedMails = function() {
        var selected = [];
        angular.forEach($scope.getMails(), function(mail) {
            if(mail.selected) {
                selected.push(mail);
            }
        });
		return selected;
	};





    $scope.$on('folder-intent-done', function(e) {
        $scope.updateOverview();
    });



    $scope.$on('mark-as-read', function(e) {
        _updateFlags(function(mail) {
            if(!mail.read) {
                mail.read = true;
                return mail;
            }
        });
    });

    $scope.$on('mark-as-unread', function(e) {
        _updateFlags(function(mail) {
            if(mail.read) {
                mail.read = false;
                return mail;
            }
        });
    });

    $scope.$on('add-star', function(e) {
        _updateFlags(function(mail) {
            if(!mail.starred) {
                mail.starred = true;
                return mail;
            }
        });
    });

    $scope.$on('remove-star', function(e) {
        _updateFlags(function(mail) {
            if(mail.starred) {
                mail.starred = false;
                return mail;
            }
        });
    });

	$scope.updateOverview = function() {
		MailResource.findByFolder({
			folder: $scope.currentFolder,
			page: $scope.currentPage
		}).$promise
		.then(function(data) {
			$scope.data = data;
		});
	};

	$scope.showIcon = function(mail) {
		return mail.answered || mail.forwarded;
	};

	$scope.whichIcon = function(mail) {
		if(mail.answered && mail.forwarded) {
			return "forwardedanswered";
		} else if(mail.answered) {
			return "answered";
		} else {
			return "forwarded"
		}
	};

	$scope.hasMailSelected = function() {
		return $scope.getSelectedMails().length !== 0;
	};
	
	$scope.deleteMails = function() {
		var selectedMails = $scope.getSelectedMails();

		MailResource.deleteMails(selectedMails).$promise
		.then(function() {
			$rootScope.$broadcast('notification', i18nService.resolve("Message(s) deleted"));
			$scope.updateOverview();
		});
	};
	
	$scope.clickStar = function() {
		var mail = this.mail;		
		mail.starred = !mail.starred;
		
		MailResource.updateFlags(this.mail).$promise
		.catch(function() {
			mail.starred = !mail.starred;
		});
	};

    $scope.open = function(mail) {
        //TODO drafts!
        $location.url("/message?id=" + mail.id);
    };

    $scope.updateOverview();
})
.controller('FolderSettingsCtrl', function($scope, $rootScope, $location, FolderResource, INITIAL_MAILBOX) {
    $scope.currentFolder;

    $scope.refresh = function() {
        FolderResource.findAll().then(function(folders) {
            $scope.folders = folders;

            if($scope.currentFolder !== undefined) {
                return;
            }

            angular.forEach(folders, function(folder) {
                if(folder.id === INITIAL_MAILBOX) {
                    $scope.currentFolder = folder;
                }
            });
        });
    };

    $scope.selectFolder = function(folder) {
        $scope.currentFolder = folder;
    };

    $scope.createFolder = function() {
        if($scope.folderName !== "" && $scope.folderName !== undefined) {
            FolderResource.create({'id': $scope.currentFolder.id, 'folder' : $scope.folderName}).then(function(result) {
                $rootScope.$broadcast('folder-created', folder);
                $scope.refresh();
            });
        } else {
            $rootScope.$broadcast('notification', i18nService.resolve("Enter a folder name!"));
        }
    };

    $scope.deleteFolder = function(folder) {
        //TODO replace me
        if (confirm('Move folder "' + folder.name + '" to trash?')) {
            FolderResource.delete(folder.id).then(function() {
                $rootScope.$broadcast('folder-deleted', folder);
                $scope.refresh();
            });
        }
    };

    $scope.toggleSubscription = function(folder) {
        folder.subscribed = !folder.subscribed;
        FolderResource.save(folder).then(function() {
            $rootScope.$broadcast('folder-updated', folder);
        });
    };

    $scope.renameFolder = function(folder) {
        alert("not implemented yet");
    };

    $scope.refresh();

})
.controller('MessageCtrl', function($scope, $rootScope, $routeParams, routeService, MailResource, i18nService) {
    $scope.mail;


    function _updateFlags(mail) {
        MailResource.updateFlags([mail]).$promise
        .catch(function() {
            $scope.updateOverview();
        });

        $rootScope.$broadcast("more-actions-done");
    }

    $scope.$on('folder-intent-done', function(e, folderAction) {
        if(folderAction === "move") {
            $rootScope.$broadcast('notification', i18nService.resolve("Message moved"));
            routeService.navigateToPrevious();
        } else {
            $rootScope.$broadcast('notification', i18nService.resolve("Message copied"));
        }
    });

    $scope.getSelectedMails = function() {
        var selected = [];
        if($scope.mail !== undefined) {
            selected.push($scope.mail);
        }
        return selected;
    };

    $scope.hasMailSelected = function() {
        return $scope.getSelectedMails().length > 0;
    };

    $scope.deleteMails = function() {
        if (!confirm('Do you really want to delete this message?')) {
            return;
        }

        MailResource.delete($scope.mail.id)
        .then(function() {
            $rootScope.$broadcast('notification', i18nService.resolve("Message deleted"));
            routeService.navigateToPrevious();
        });
    };

    $scope.$on('mark-as-read', function(e) {
        $scope.mail.read = true;
        _updateFlags($scope.mail);
    });

    $scope.$on('mark-as-unread', function(e) {
        $scope.mail.read = false;
        _updateFlags($scope.mail);
    });

    $scope.$on('add-star', function(e) {
        $scope.mail.starred = true;
        _updateFlags($scope.mail);
    });

    $scope.$on('remove-star', function(e) {
        $scope.mail.starred = false;
        _updateFlags($scope.mail);
    });

    $scope.refresh = function() {
        MailResource.load($routeParams.id).then(function(mail) {
            $scope.mail = mail;
        })
        .catch(function(e) {
            $rootScope.$broadcast('error', i18nService.resolve("Message does not exists"));
            routeService.navigateTo("box");
        });
    };

    $scope.refresh();
})
.controller('ComposerCtrl', function($scope, $routeParams, MailResource) {
    $scope.mail;

    $scope.refresh = function() {
        MailResource.load($routeParams.id).then(function(mail) {
            $scope.mail = mail;
        });
    };

    $scope.refresh();
});