
function FolderListCtrl($scope, $rootScope, FolderResource) {
	
	FolderResource.findAll().$promise.then(function(folders) {
		$scope.folders = folders;
	});
	
	$scope.reset = function() {
		$scope.query = null;
	}

    $scope.onFolderSelect = function(selectedFolder) {
       $rootScope.$broadcast('folder-intent', selectedFolder);
    }
}

function MailOverviewCtrl($scope, $window, $location, $rootScope, MailResource, i18nService, INITIAL_MAILBOX) {

	$scope.currentFolder = INITIAL_MAILBOX;
	$scope.selected = [];
	$scope.folderIntent;
	
	$scope.pager = {
		currentPage : 1,
		pages: 1,
		length: 20,
		fullLength: 0
	};
	
	function _findMailByFolder() {
		MailResource.findByFolder({
			folder: $scope.currentFolder, 
			page: $scope.pager.currentPage
		}).$promise
		.then(function(data) {
			$scope.mails = data.mails;
			$scope.pager = data.pagination;		
		});
	}

    function _folderIntentDone() {
        _findMailByFolder();
        $rootScope.$broadcast('folder-intent-done');
        $scope.folderIntent = null;
    };

	function getSelectedMails() {
		var selected = [];

		angular.forEach($scope.mails, function(mail) {
			if(mail.selected) {
				selected.push(mail);
			}
		});

		return selected;
	}

	function selectAll(flag) {
		angular.forEach($scope.mails, function(mail) {
			mail.selected = flag;
		});
	}
	
	$scope.$on('$locationChangeSuccess', function(event) {
		//TODO is there a better way?
		var hash = ($window.location.hash.length !== 0) ? $window.location.hash.substring(2) : INITIAL_MAILBOX;
		
		if($scope.currentFolder !== hash) {
			$scope.pager.currentPage= 1;
			$scope.currentFolder = hash;
		}

		_findMailByFolder();
	});

    $scope.$on('folder-intent', function(e, folder) {
        var params = {folder: folder, mails: getSelectedMails()};

        switch($scope.folderIntent)    {
            case "copy": MailResource.copy(params).$promise.then(_folderIntentDone); break;
            case "move": MailResource.move(params).$promise.then(_folderIntentDone); break;
        }
    });

    $scope.moveToFolder = function() {
        $scope.folderIntent = "move";
    }

    $scope.copyToFolder = function() {
        $scope.folderIntent = "copy";
    }

	$scope.firstPage = function() {
		$scope.pager.currentPage = 1;		
		_findMailByFolder();		
	}
	
	$scope.lastPage = function() {
		$scope.pager.currentPage = $scope.pager.pages;		
		_findMailByFolder();		
	}
	
	$scope.nextPage = function() {
		$scope.pager.currentPage = $scope.pager.currentPage+1;		
		_findMailByFolder();		
	}
	
	$scope.previousPage = function() {
		$scope.pager.currentPage = $scope.pager.currentPage-1		
		_findMailByFolder();	
	}
	
	$scope.showIcon = function(mail) {
		return mail.answered || mail.forwarded;
	}
	
	$scope.isFirstPage = function() {
		return $scope.pager.currentPage === 1 ;
	}
	
	$scope.isLastPage = function() {
		return $scope.pager.currentPage === $scope.pager.pages;
	}
	
	$scope.whichIcon = function(mail) {
		if(mail.answered && mail.forwarded) {
			return "forwardedanswered";
		} else if(mail.answered) {
			return "answered";
		} else {
			return "forwarded"
		}
	}

	$scope.selectAll = function() {
		selectAll(true);
	}
	
	$scope.selectNone = function() {
		selectAll(false);
	}
	
	$scope.hasMailSelected = function() {
		var hasSelection = getSelectedMails().length === 0;
		
		if(hasSelection) {
			$rootScope.$broadcast("mail.selection");
		} else {
			$rootScope.$broadcast("mail.noselection");
		}
		
		return hasSelection;
	}
	
	$scope.deleteMails = function() {
		var selectedMails = getSelectedMails();

		MailResource.deleteMails(selectedMails).$promise
		.then(function() {
			$rootScope.$broadcast('notification', i18nService.resolve("Message(s) deleted"));
			_findMailByFolder();			
		});
	}
	
	$scope.clickStar = function() {
		var mail = this.mail;		
		mail.starred = !mail.starred;
		
		MailResource.updateFlags(this.mail).$promise
		.catch(function() {
			mail.starred = !mail.starred;
		});
	}
	
	/*
	$scope.moveTo = function() {
		$rootScope.$broadcast("");
	}
	*/
}