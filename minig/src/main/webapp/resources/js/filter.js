
app.filter('timeago', function(timeService) {
	
	return function(date) {
		if(date) {
			try {
				return timeService.humanReadableAbbr(date);
			} catch(e) {
				return "";
			}
		} else {
			return "";
		}
	}
});

app.filter('i18n', function(i18nService) {
	
	return function(text) {
		return i18nService.resolve(text);
	}
});