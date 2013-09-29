function FolderListCtrl($scope, $http) {
	
	$http.get('api/1/folder').success(function(data) {
		$scope.folders = data.folderList;
	});
}

function MailOverviewCtrl($scope, $http, $window, $location) {
	
	$scope.$on('$locationChangeSuccess', function(event) {
		var hash = $window.location.hash;		
		var folder = (hash.length == 0) ? "INBOX" : hash.substring(2);
	    
		$http.get('api/1/message?folder='+folder).success(function(data) {
			$scope.mails = data.mailList;
		});
	});
	
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