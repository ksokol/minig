
app.service('timeService',['$q', '$timeout', '$window', function($q, $timeout, $window) {

	return {
		
		humanReadableAbbr: function(date) {
			return moment(date).fromNow();
		}
	
	}
}]);