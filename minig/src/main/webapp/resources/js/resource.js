
app.factory('FolderResource', function($q, $resource, folderCache, API_HOME) {

	var folderResourceGet =  $resource(API_HOME + 'folder/:id', {}, {_findAll : {method: 'GET', isArray: true, transformResponse: _transFindAll}});
    var folderResourcePost =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_create : {method: 'POST'}});
    var folderResourceDelete =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_delete : {method: 'DELETE'}});
    var folderResourcePut =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_put : {method: 'PUT'}});

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

    folderResourceDelete.delete = function(id) {
        var deferred = $q.defer();
        folderResourceDelete._delete({'id': encodeURIComponent(id)}).$promise.then(function(result) {
            folderCache.clear();
            deferred.resolve();
        });
        return deferred.promise;
    };

    folderResourcePut.save = function(data) {
        var deferred = $q.defer();
        var copy = angular.copy(data);
        copy.id = encodeURIComponent(copy.id);

        folderResourcePut._put(copy).$promise.then(function(result) {
            folderCache.update(data);
            deferred.resolve();
        });
        return deferred.promise;
    }

    return {
        findAll : folderResourceGet.findAll,
        create : folderResourcePost.create,
        delete : folderResourceDelete.delete,
        save : folderResourcePut.save
    }

});

app.factory('MailResource', ['$q', '$resource','API_HOME','DEFAULT_PAGE_SIZE', function($q, $resource, API_HOME, DEFAULT_PAGE_SIZE) {
	var defaults = {page: 1, page_length: DEFAULT_PAGE_SIZE};
	
	var messageDelete = $resource(API_HOME+'message/delete', {}, {deleteMails: {method: 'PUT', isArray:true, transformRequest: _transMessageDelete }});
	var messageByFolder = $resource(API_HOME+'message?folder=:folder', defaults, {findByFolder: {method: 'GET'}});
	var messageUpdateFlag = $resource(API_HOME+'message/flag', {}, {updateFlags: {method: 'PUT', isArray:true, transformRequest: _transMessageUpdateFlags}});
	var messageMove = $resource(API_HOME+'message/move', {}, {moveMessage: {method: 'PUT', isArray:true, transformRequest: _transMessageMoveCopy}});
	var messageCopy = $resource(API_HOME+'message/copy', {}, {copyMessage: {method: 'PUT', isArray:true, transformRequest: _transMessageMoveCopy}});
    var messageLoad = $resource(API_HOME+'message/:id', {'id':'@id'}, {_load: {method: 'GET'}});
    var messageDeleteSingle = $resource(API_HOME+'message/:id', {'id':'@id'}, {_delete: {method: 'DELETE' }});

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

    messageLoad.load = function(id) {
        var deferred = $q.defer();
        messageLoad._load({ 'id' : encodeURIComponent(id)}).$promise.then(function(result) {
            deferred.resolve(result);
        })
        .catch(function(error) {
            deferred.reject(error);
        });
        return deferred.promise;
    };

    messageDeleteSingle.delete = function(id) {
        var deferred = $q.defer();
        messageDeleteSingle._delete({ 'id' : encodeURIComponent(id)}).$promise.then(function(result) {
            deferred.resolve(result);
        });
        return deferred.promise;
    };
	
	return {
		findByFolder: messageByFolder.findByFolder,
		deleteMails: messageDelete.deleteMails,
		updateFlags: messageUpdateFlag.updateFlags,
		move: messageMove.moveMessage,
		copy: messageCopy.copyMessage,
        load: messageLoad.load,
        delete: messageDeleteSingle.delete
	};
	
}]);

app.factory('AttachmentResource', ['$q', '$resource','API_HOME', function($q, $resource, API_HOME) {

    var attachmentGet = $resource(API_HOME+'attachment/:id', {'id' : '@id'}, {findById: {method: 'GET' }});

    var findById = function(id) {
        var deferred = $q.defer();
        attachmentGet.findById({ 'id' : encodeURIComponent(id)}).$promise.then(function(result) {
            deferred.resolve(result);
        });
        return deferred.promise;
    };

    return {
        findById: findById
    };

}]);