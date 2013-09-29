function FolderListCtrl($scope, $http) {
	
	//TODO service
	$http.get('api/1/folder').success(function(data) {
		$scope.folders = data.folderList;
	});
}

//TODO remove hardcoded stuff
function MailOverviewCtrl($scope, $http, $window, $location) {
	$scope.currentFolder = "INBOX";
	$scope.currentPage = 1;
	$scope.isLastPage = false;
	
	$scope.$on('$locationChangeSuccess', function(event) {
		var hash = $window.location.hash;		
		$scope.currentFolder = (hash.length == 0) ? "INBOX" : hash.substring(2);
	    
		//TODO service
		$http.get('api/1/message?page_length=20&folder='+ $scope.currentFolder).success(function(data) {
			$scope.mails = data.mailList;
			$scope.isLastPage = data.mailList.length < 20 || data.fullLength == 20;
		});
	});
	
	$scope.nextPage = function() {
		$scope.currentPage = $scope.currentPage+1;
		
		//TODO service
		$http.get('api/1/message?page_length=20&folder='+ $scope.currentFolder+ "&page="+ $scope.currentPage).success(function(data) {
			$scope.mails = data.mailList;
			$scope.isLastPage = data.mailList.length < 20 || data.fullLength == 20;
		});			
	}
	
	$scope.previousPage = function() {
		$scope.currentPage = $scope.currentPage-1;
		
		//TODO service
		$http.get('api/1/message?page_length=20&folder='+ $scope.currentFolder+ "&page="+ $scope.currentPage).success(function(data) {
			$scope.mails = data.mailList;
			$scope.isLastPage = data.mailList.length < 20 || data.fullLength == 20;
		});			
	}
	
	$scope.showIcon = function(mail) {
		return mail.answered || mail.forwarded;
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
}