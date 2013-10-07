
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

app.factory('MailResource', function($resource, API_HOME) {
	var defaults = {page: 1, page_length: 20};
	
	var messageDelete = $resource(API_HOME+'message/delete', {}, {deleteMails: {method: 'PUT', isArray:true, transformRequest: _transMessageDelete }});
	var messageByFolder = $resource(API_HOME+'message?folder=:folder', defaults, {findByFolder: {method: 'GET', transformResponse: _transMessageByFolder}});
		
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
			var pagination = {};
			
			pagination.currentPage = json.page;
			pagination.pageLength = defaults.page_length;
			pagination.fullLength = json.fullLength;
			pagination.pages = parseInt(pagination.fullLength / pagination.pageLength) + ((defaults.page_length % json.pageLength !== 0) ? 1 : 0);
			
			var start = (pagination.currentPage === 1) ? 1 : (pagination.currentPage -1) * pagination.pageLength;
			var mul = pagination.currentPage * pagination.pageLength;
			var end = (mul > pagination.fullLength) ? pagination.fullLength : mul;
			
			pagination.start = start;
			pagination.end = end;
			
			return {pagination: pagination, mails: json.mailList};
		} catch(e) {}
	}
	
	return {
		findByFolder: messageByFolder.findByFolder,
		deleteMails: messageDelete.deleteMails
	};
	
});