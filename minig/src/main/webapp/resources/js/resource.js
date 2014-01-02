
app.factory('FolderResource', function($resource, API_HOME) {
	var folderResource =  $resource(API_HOME + 'folder/:id', {}, {_findAll : {method: 'GET', isArray: true, transformResponse: _transFindAll}});
	
	function _transFindAll(data) {
		try {
			var json = angular.fromJson(data);
			return json.folderList;
		} catch(e) {}
	}
	
	folderResource.findAll = function() {
		return folderResource._findAll();
	}
	
	return folderResource;
});

app.factory('MailResource', ['$resource','API_HOME','DEFAULT_PAGE_SIZE','pagerFactory', function($resource, API_HOME, DEFAULT_PAGE_SIZE, pagerFactory) {
	var defaults = {page: 1, page_length: DEFAULT_PAGE_SIZE};
	
	var messageDelete = $resource(API_HOME+'message/delete', {}, {deleteMails: {method: 'PUT', isArray:true, transformRequest: _transMessageDelete }});
	var messageByFolder = $resource(API_HOME+'message?folder=:folder', defaults, {findByFolder: {method: 'GET', transformResponse: _transMessageByFolder}});
	var messageUpdateFlag = $resource(API_HOME+'message/flag', {}, {updateFlags: {method: 'PUT', isArray:true, transformRequest: _transMessageUpdateFlags}});
	var messageMove = $resource(API_HOME+'message/move', {}, {moveMessage: {method: 'PUT', isArray:true, transformRequest: _transMessageMoveCopy}});
	var messageCopy = $resource(API_HOME+'message/copy', {}, {copyMessage: {method: 'PUT', isArray:true, transformRequest: _transMessageMoveCopy}});

	function _transMessageDelete(mails) {
		var idList = [];
		
		angular.forEach(mails, function(value) {
			idList.push(value.id);
		});
		
		return angular.toJson({messageIdList : idList });
	}
	
	function _transMessageByFolder(data, headersGetter) {
		try {
			var json = angular.fromJson(data);
            var pager = pagerFactory.newInstance(json.page, json.pageLength, json.fullLength);

			return {pagination: pager, mails: json.mailList};
		} catch(e) {}
	}
	
	function _transMessageUpdateFlags(mails) {
		var toSend = [];
        mails = (angular.isArray(mails)) ? mails : [mails];

		angular.forEach(mails, function(mail) {
            var copy = angular.copy(mail, {});

            delete copy.body;
            delete copy.sender;
            delete copy.selected;

            toSend.push(copy);
		});

		return angular.toJson({mailList: toSend});
	}

	function _transMessageMoveCopy(data) {
        var folder = data['folder'].id;
        var ids = [];

        angular.forEach(data['mails'], function(mail) { ids.push(mail.id); } );

        return angular.toJson({folder: folder, messageIdList: ids});
	}
	
	return {
		findByFolder: messageByFolder.findByFolder,
		deleteMails: messageDelete.deleteMails,
		updateFlags: messageUpdateFlag.updateFlags,
		move: messageMove.moveMessage,
		copy: messageCopy.copyMessage
	};
	
}]);