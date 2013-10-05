       
function FolderListCtrl($scope, $http) {
	
	//TODO service
	$http.get('api/1/folder').success(function(data) {
		$scope.folders = data.folderList;
	});
	
	$scope.reset = function() {
		$scope.query = null;
	}
}

//TODO remove hardcoded stuff
function MailOverviewCtrl($scope, $http, $window, $location) {
	//TODO
	$scope.currentFolder = "INBOX";
	$scope.currentPage = 1;
	$scope.pages = 1;
	$scope.pageLength = 20;
	$scope.fullLength = 0;
	
	$scope.setPager = function (pager) {
		var length = pager.fullLength;
		
		$scope.currentPage = pager.page;
		$scope.fullLength = pager.fullLength;
		$scope.pages = parseInt(length / $scope.pageLength) + ((length % $scope.pageLength !== 0) ? 1 : 0);
	}
	
	$scope.pageStatus = function() {		
		var start = ($scope.currentPage === 1) ? 1 : ($scope.currentPage -1) * $scope.pageLength;
		var mul = $scope.currentPage * $scope.pageLength;
		var end = (mul > $scope.fullLength) ? $scope.fullLength : mul;
		
		var status = start + " - "  + end + " of " +$scope.fullLength;
		
		return status;
	}
	
	$scope.$on('$locationChangeSuccess', function(event) {
		var hash = $window.location.hash;		
		$scope.currentFolder = (hash.length == 0) ? "INBOX" : hash.substring(2);
	    
		//TODO service
		$http.get('api/1/message?page_length=' + $scope.pageLength + '&folder='+ $scope.currentFolder).success(function(data) {
			$scope.mails = data.mailList;
			$scope.setPager(data);			
		});
	});
	
	$scope.firstPage = function() {
		$scope.currentPage = 1;
		
		//TODO service
		$http.get('api/1/message?page_length=' + $scope.pageLength + '&folder='+ $scope.currentFolder+ "&page="+ $scope.currentPage).success(function(data) {
			$scope.mails = data.mailList;
			$scope.setPager(data);
		});			
	}
	
	$scope.lastPage = function() {
		$scope.currentPage = $scope.pages;
		
		//TODO service
		$http.get('api/1/message?page_length=' + $scope.pageLength + '&folder='+ $scope.currentFolder+ "&page="+ $scope.currentPage).success(function(data) {
			$scope.mails = data.mailList;
			$scope.setPager(data);
		});			
	}
	
	$scope.nextPage = function() {
		$scope.currentPage = $scope.currentPage+1;
		
		//TODO service
		$http.get('api/1/message?page_length=' + $scope.pageLength + '&folder='+ $scope.currentFolder+ "&page="+ $scope.currentPage).success(function(data) {
			$scope.mails = data.mailList;
			$scope.setPager(data);
		});			
	}
	
	$scope.previousPage = function() {
		$scope.currentPage = $scope.currentPage-1;
				
		//TODO service
		$http.get('api/1/message?page_length=' + $scope.pageLength + '&folder='+ $scope.currentFolder+ "&page="+ $scope.currentPage).success(function(data) {
			$scope.mails = data.mailList;
			$setPager(data);
		});			
	}
	
	$scope.showIcon = function(mail) {
		return mail.answered || mail.forwarded;
	}
	
	$scope.isFirstPage = function() {
		return $scope.currentPage === 1 ;
	}
	
	$scope.isLastPage = function() {
		return $scope.currentPage === $scope.pages;
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