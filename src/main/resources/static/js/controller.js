app.controller('FolderListCtrl', ['$scope', '$rootScope', 'folderService', 'routeService', 'INITIAL_MAILBOX', function($scope, $rootScope, folderService, routeService, INITIAL_MAILBOX) {
    $scope.folders = [];

    folderService.findAll().then(function (folders) {
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
        folderService.findAll().then(function(folders) {
            $scope.folders = folders;
        });
    };

    $scope.selectFolder = function(folder) {
        routeService.navigateTo({path: 'box', params: {folder: folder || INITIAL_MAILBOX, page : 0}, reload: true});
    };

    $scope.composer = function() {
        routeService.navigateTo({path: 'composer', reload: true});
    };
}])
.controller('MailOverviewCtrl', ['$scope', '$rootScope', '$routeParams', 'mailService', 'i18nService', 'draftService', 'routeService', 'INITIAL_MAILBOX', function($scope, $rootScope, $routeParams, mailService, i18nService, draftService, routeService, INITIAL_MAILBOX) {

	$scope.currentFolder = $routeParams.folder || INITIAL_MAILBOX;
	$scope.currentPage = $routeParams.page || 0;
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
           mailService.updateFlags(tmp)
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

    $scope.$on('folder-intent-done', function() {
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
		mailService.findByFolder({
			folder: $scope.currentFolder,
			page: $scope.currentPage
		})
		.then(function(data) {
			$scope.data = data;
            routeService.navigateTo({path:"box", params: {folder: $scope.currentFolder, page: $scope.currentPage}});
		});
	};

	$scope.showIcon = function(mail) {
		return mail.answered || mail.forwarded;
	};

	$scope.whichIcon = function(mail) {
        if($scope.showIcon(mail)) {
            if(mail.answered && mail.forwarded) {
                return "forwardedanswered";
            } else if(mail.answered) {
                return "answered";
            } else {
                return "forwarded"
            }
        }
	};

	$scope.hasMailSelected = function() {
		return $scope.getSelectedMails().length !== 0;
	};
	
	$scope.deleteMails = function() {
		var selectedMails = $scope.getSelectedMails();

		mailService.deleteMails(selectedMails)
		.then(function() {
			$rootScope.$broadcast('notification', i18nService.resolve("Message(s) deleted"));
			$scope.updateOverview();
		});
	};
	
	$scope.clickStar = function() {
		var mail = this.mail;		
		mail.starred = !mail.starred;
		
		mailService.updateFlags(this.mail)
		.catch(function() {
			mail.starred = !mail.starred;
		});
	};

    $scope.open = function(mail) {
        if(draftService.isDraft(mail)) {
            routeService.navigateTo({path:"composer", params: {id: mail.id }});
            return;
        }
        routeService.navigateTo({path:"message", params: {id: mail.id }});
    };

    $scope.updateOverview();
}])
.controller('FolderSettingsCtrl', ['$scope', '$rootScope', '$location', 'folderService', 'INITIAL_MAILBOX', function($scope, $rootScope, $location, folderService, INITIAL_MAILBOX) {
    $scope.currentFolder;

    $scope.refresh = function() {
        folderService.findAll().then(function(folders) {
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
            folderService.create({'id': $scope.currentFolder.id, 'folder' : $scope.folderName}).then(function(result) {
                $rootScope.$broadcast('folder-created', result);
                $scope.refresh();
            });
        } else {
            $rootScope.$broadcast('notification', i18nService.resolve("Enter a folder name!"));
        }
    };

    $scope.deleteFolder = function(folder) {
        //TODO replace me
        if (confirm('Move folder "' + folder.name + '" to trash?')) {
            folderService.delete(folder.id).then(function() {
                $rootScope.$broadcast('folder-deleted', folder);
                $scope.refresh();
            });
        }
    };

    $scope.toggleSubscription = function(folder) {
        folder.subscribed = !folder.subscribed;
        folderService.save(folder).then(function() {
            $rootScope.$broadcast('folder-updated', folder);
        });
    };

    $scope.renameFolder = function(folder) {
        alert("not implemented yet");
    };

    $scope.refresh();

}])
.controller('MessageCtrl', ['$scope', '$rootScope', '$routeParams', 'routeService', 'mailService', 'i18nService', 'draftService', 'composerService', function($scope, $rootScope, $routeParams, routeService, mailService, i18nService, draftService, composerService) {
    $scope.mail;

    function _updateFlags(mail) {
        mailService.updateFlags([mail])
        .catch(function() {
            $rootScope.$broadcast("error", i18nService.resolve("something went wrong"));
        });

        $rootScope.$broadcast("more-actions-done");
    };

    function saveAndNavigateToComposer(mail) {
        draftService.save(mail)
        .then(function(savedMail) {
            routeService.navigateTo({path:"composer", params: {id: savedMail.id }});
        });
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

        mailService.delete($scope.mail.id)
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

    $scope.clickStar = function() {
        $scope.mail.starred = !$scope.mail.starred;

        mailService.updateFlags($scope.mail)
        .catch(function() {
            $scope.mail.starred = !$scope.mail.starred;
        });
    };

    $scope.refresh = function() {
        mailService.load($routeParams.id).then(function(mail) {
            $scope.mail = mail;
            if(!$scope.mail.read) {
                $scope.mail.read = true;
                _updateFlags($scope.mail);
            }
        })
        .catch(function(e) {
            $rootScope.$broadcast('error', i18nService.resolve("Message does not exists"));
            routeService.navigateTo("box");
        });
    };

    $scope.reply = function() {
        var reply = composerService.reply($scope.mail);
        saveAndNavigateToComposer(reply);
    };

    $scope.replyToAll = function() {
        var replyToAll = composerService.replyToAll($scope.mail);
        saveAndNavigateToComposer(replyToAll);
    };

    $scope.forward = function() {
        var forward = composerService.createForward($scope.mail);
        saveAndNavigateToComposer(forward);
    };

    $scope.refresh();
}])
.controller('ComposerCtrl', ['$scope', '$rootScope', '$routeParams', 'mailService', 'draftService', 'composerService', 'routeService', 'i18nService', 'submissionService', function($scope, $rootScope, $routeParams, mailService, draftService, composerService, routeService, i18nService, submissionService) {
    $scope.mail = {
        to : [],
        cc: [],
        bcc : []
    };

    $scope.htmlBody;

    $scope.refresh = function() {
        if(!$routeParams.id) {
            return;
        }
        mailService.load($routeParams.id).then(function(mail) {
            if(draftService.isDraft(mail)) {
                $scope.mail = mail;
                $scope.showCc = $scope.mail.cc.length > 0;
                $scope.showBcc = $scope.mail.bcc.length > 0;

                mailService.loadHtml(mail.id).then(function(htmlBody) {
                    $scope.htmlBody = htmlBody;
                });

                return;
            }
            $rootScope.$broadcast("error", i18nService.resolve("This is not a draft"));
            routeService.navigateTo({path: 'box'});
        });
    };

    $scope.send = function() {
        submissionService.submission($scope.mail)
        .then(function() {
            routeService.navigateToPrevious();
            $rootScope.$broadcast('notification', i18nService.resolve("Mail sent"));
        });
    };

    $scope.save = function() {
        draftService.save($scope.mail)
        .then(function(mail) {
            $scope.mail = mail;
            $rootScope.$broadcast('notification', i18nService.resolve("Draft saved"));
        });
    };

    $scope.discard = function() {
        routeService.navigateToPrevious();
    };

    $scope.delete = function() {
        if (!confirm('Do you really want to delete this message?')) {
            return;
        }

        mailService.delete($scope.mail.id)
        .then(function() {
            $rootScope.$broadcast('notification', i18nService.resolve("Message deleted"));
            routeService.navigateToPrevious();
        });
    };

    $scope.$watch("mail.id", function(id) {
        if(!id) {
            return;
        }

        $scope.mail.id = id;
        routeService.navigateTo({path:"composer", params: {id: id }});
    });

    $scope.refresh();
}]);
