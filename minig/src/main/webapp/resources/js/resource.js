
app.factory('FolderResource', function($q, $resource, folderCache, API_HOME) {
    //var cache = [];

	var folderResourceGet =  $resource(API_HOME + 'folder/:id', {}, {_findAll : {method: 'GET', isArray: true, transformResponse: _transFindAll}});
    var folderResourcePost =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_create : {method: 'POST'}});

    function _transFindAll(data) {
		try {
			var json = angular.fromJson(data);
			return json.folderList;
		} catch(e) {}
	}

	folderResourceGet.findAll = function() {
        var deferred = $q.defer();

        if(!folderCache.isEmpty()) {
            deferred.resolve(folderCache.snapshot());
        } else {
            folderResourceGet._findAll().$promise.then(function(folders) {
                folderCache.fill(folders);
                deferred.resolve(folderCache.snapshot());
            });
        }

        return deferred.promise;
	};

    folderResourcePost.create = function(data) {
        var deferred = $q.defer();

        folderResourcePost._create({'id': encodeURIComponent(data.id), 'folder' : data.folder}).$promise.then(function(result) {
            folderCache.add(result);
            deferred.resolve(result);
        });

        return deferred.promise;
    };


    return {
        findAll : folderResourceGet.findAll,
        create : folderResourcePost.create
    }

});

app.factory('MailResource', ['$resource','API_HOME','DEFAULT_PAGE_SIZE','pagerFactory', function($resource, API_HOME, DEFAULT_PAGE_SIZE, pagerFactory) {
	var defaults = {page: 1, page_length: DEFAULT_PAGE_SIZE};
	
	var messageDelete = $resource(API_HOME+'message/delete', {}, {deleteMails: {method: 'PUT', isArray:true, transformRequest: _transMessageDelete }});
	var messageByFolder = $resource(API_HOME+'message?folder=:folder', defaults, {findByFolder: {method: 'GET'}});
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