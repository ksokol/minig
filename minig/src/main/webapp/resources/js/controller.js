       
function FolderListCtrl($scope, FolderResource) {
	
	FolderResource.findAll().$promise.then(function(folders) {
		$scope.folders = folders;
	});
	
	$scope.reset = function() {
		$scope.query = null;
	}
}

function MailOverviewCtrl($scope, $window, $location, $rootScope, MailResource, i18nService, INITIAL_MAILBOX) {

	$scope.currentFolder = INITIAL_MAILBOX;
	$scope.selected = [];
	
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

	function getSelectedMails() {
		var selected = [];

		angular.forEach($scope.mails, function(mail) {
			if(mail.selected === true) {
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
		return getSelectedMails().length === 0;
	}
	
	$scope.deleteMails = function() {
		var selectedMails = getSelectedMails();

		MailResource.deleteMails(selectedMails).$promise
		.then(function() {
			$rootScope.$broadcast('notification', i18nService.resolve("Message(s) deleted"));
			_findMailByFolder();			
		});
	}
	
	
	
	$scope.moveTo = function() {
		console.log("move");
	}
}