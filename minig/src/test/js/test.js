
describe('FolderListCtrl', function(){
    var scope;//we'll use this scope in our tests
    var $httpBackend, fixtureFolderlist;

    beforeEach(module('minigApp', 'fixture/folderlist.json'));

    beforeEach(inject(function($rootScope, $controller, API_HOME, _$httpBackend_, _fixtureFolderlist_){
       $httpBackend = _$httpBackend_;
       fixtureFolderlist = _fixtureFolderlist_;

       _$httpBackend_.whenGET(API_HOME+'folder').respond(_fixtureFolderlist_);

       scope = $rootScope.$new();

       $controller('FolderListCtrl', {$scope: scope});
    }));

    it('should have correct size', function() {
        expect(scope.folders.length).toBe(0);
        $httpBackend.flush();
        expect(scope.folders.length).toBe(5);
    });

    it('should have same json structure', function() {
        $httpBackend.flush();
        expect(angular.toJson(scope.folders)).toEqual(angular.toJson(fixtureFolderlist.folderList));
    });
});