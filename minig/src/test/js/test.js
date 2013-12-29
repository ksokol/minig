
describe('FolderListCtrl', function(){
    var scope;//we'll use this scope in our tests
    var $httpBackend;

    beforeEach(module('minigApp', 'folderlist.json'));

    beforeEach(inject(function($rootScope, $controller, API_HOME, _$httpBackend_, _folderlist_){
       $httpBackend = _$httpBackend_;
       _$httpBackend_.whenGET(API_HOME+'folder').respond(_folderlist_);

        scope = $rootScope.$new();

        $controller('FolderListCtrl', {$scope: scope});
    }));

    it('scope should have folders.length == 5', function() {
        expect(scope.folders.length).toBe(0);
        $httpBackend.flush();
        expect(scope.folders.length).toBe(5);
    });

});