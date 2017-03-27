//TODO replace $resource with $http
app.factory('folderService', ['$q', '$resource', 'deferService', 'folderCache', 'API_HOME', function($q, $resource, deferService, folderCache, API_HOME) {

	var folderResourceGet =  $resource(API_HOME + 'folder/:id', {}, {_findAll : {method: 'GET', isArray: true}});
    var folderResourcePost =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_create : {method: 'POST'}});
    var folderResourceDelete =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_delete : {method: 'DELETE'}});
    var folderResourcePut =  $resource(API_HOME + 'folder/:id', {'id':'@id'}, {_put : {method: 'PUT'}});

	folderResourceGet.findAll = function() {
        if(!folderCache.isEmpty()) {
            return deferService.resolved(folderCache.snapshot());
        }
        var deferred = deferService.deferred(folderResourceGet._findAll);
        deferred.then(function(folders) {
            folderCache.fill(folders);

        });
        return deferred;
	};

    folderResourcePost.create = function(data) {
        var deferred = deferService.deferred(folderResourcePost._create, {'id': encodeURIComponent(data.id), 'folder' : data.folder});
        deferred.then(function(result) {
            folderCache.add(result);
        });
        return deferred;
    };

    folderResourceDelete.delete = function(id) {
        var deferred = deferService.deferred(folderResourceDelete._delete, {'id': encodeURIComponent(id)});
        deferred.then(function() {
            folderCache.clear();
        });
        return deferred;
    };

    folderResourcePut.save = function(data) {
        var copy = angular.copy(data);
        copy.id = encodeURIComponent(copy.id);
        var deferred = deferService.deferred(folderResourcePut._put, copy);
        deferred.then(function(result) {
            folderCache.update(result);
        });
        return deferred;
    };

    return {
        findAll : folderResourceGet.findAll,
        create : folderResourcePost.create,
        delete : folderResourceDelete.delete,
        save : folderResourcePut.save
    }

}]);

//TODO replace $resource with $http
app.factory('mailService', ['$resource', '$http', 'deferService', 'API_HOME','DEFAULT_PAGE_SIZE', function($resource, $http, deferService, API_HOME, DEFAULT_PAGE_SIZE) {
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
        load: function(id) {
            return $http.get(API_HOME + 'message/' + id).then(function(result) {
                return result.data;
            });
        },
        delete: function(id) {
            return $http.delete(API_HOME + 'message/' + id);
        },
        deleteMails: function(params) {
            return deferService.deferred(messageDelete.deleteMails, params);
        },
        move: function(params) {
            return deferService.deferred(messageMove.moveMessage, params);
        },
        copy: function(params) {
            return deferService.deferred(messageCopy.copyMessage, params);
        },
        findByFolder: function(params) {
            return deferService.deferred(messageByFolder.findByFolder, params);
        },
        updateFlags: function(params) {
            return deferService.deferred(messageUpdateFlag.updateFlags, params);
        },
        htmlUrl: function(params) {
            return API_HOME + 'message/' + params.id + '/html';
        }
	};
}]);
