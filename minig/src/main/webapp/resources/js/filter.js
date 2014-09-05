
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

app.filter('dateformat', function(timeService) {

    return function(date) {
        if(date) {
            try {
                return timeService.humanReadable(date);
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

app.filter('subscribed', function() {
	
	return function(folder) {
		var filtered = [];
		
		angular.forEach(folder, function(folder) {
			if(folder.subscribed) {
				filtered.push(folder);
			}
		});
		
		return filtered;
	}
});

app.filter('displayName', function(i18nService) {

    var formatEmail = function(email) {
        if(email && email.displayName) {
            return email.displayName;
        }
        return email.email;
    };

	
	return function(sender) {
		if(!sender) {
            return i18nService.resolve("undisclosed recipient");
        }

        if(angular.isString(sender)) {
            return sender;
        }

        if(!angular.isArray(sender)) {
            return formatEmail(sender);
        }

        var formatted = "";
        for(i=0;i<sender.length;i++) {
            formatted = formatted + formatEmail(sender[i])+ ", ";
        }

        if(formatted.length === 0) {
            return i18nService.resolve("undisclosed recipient");
        }

        return formatted.substr(0, formatted.length -2);
	}
});

app.filter('prettyFolderName', function() {

	return function(folder) {
	    var count = folder.id.split("/").length -1;
        var pretty = "";

        for(i=0;i<count;i++) {
            pretty = pretty + '\u00A0\u00A0'; //"&nbsp;&nbsp;";
        }

        return pretty + folder.name;
	}
});