
app.service('timeService',['$q', '$timeout', '$window', function($q, $timeout, $window) {

	return {
		
		humanReadableAbbr: function(date) {
			return moment(date).fromNow();
		}
	
	}
}]);

app.service('i18nService',['$locale', function($locale) {

	//TODO right now hardcoded
	var translationMap = {};
	
	function resolve(id) {
		var translated = translationMap[id];
		
		if(translated) {
			return translated;
		} else {
			return id;
		}
	}
	
	$locale.resolve = resolve;
	
	return $locale;
	
}]);