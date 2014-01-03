
beforeEach(function(){
    this.addMatchers({
        toBeJsonEqual: function(expected){
            return angular.toJson(this.actual) === angular.toJson(expected);
        }
    });
});

describe('FolderListCtrl', function() {
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
        expect(scope.folders).toBeJsonEqual(fixtureFolderlist.folderList);
    });
});


describe('PagerFactory', function(){
    beforeEach(module('minigApp'));

    it('default page size should be 20', function() {
        inject(function(DEFAULT_PAGE_SIZE) {
            expect(DEFAULT_PAGE_SIZE).toBe(20);
        });
    });

    var combinations = [
        {actual: [], expected: {currentPage: 1, pageLength: 0, fullLength: 0, pages: 0, start: 0, end: 0}},
        {actual: [-4,-4,-6], expected: {currentPage: 1, pageLength: 0, fullLength: 0, pages: 0, start: 0, end: 0}},
        {actual: [0,0,0], expected: {currentPage: 1, pageLength: 0, fullLength: 0, pages: 0, start: 0, end: 0}},
        {actual: [1,1,1], expected: {currentPage: 1, pageLength: 20, fullLength: 1, pages: 1, start: 1, end: 1}},
        {actual: [1,20,30], expected: {currentPage: 1, pageLength: 20, fullLength: 30, pages: 2, start: 1, end: 20}},
        {actual: [2,20,30], expected: {currentPage: 2, pageLength: 20, fullLength: 30, pages: 2, start: 20, end: 30}},
        {actual: [1,20,20], expected: {currentPage: 1, pageLength: 20, fullLength: 20, pages: 1, start: 1, end: 20}},
        {actual: [1,20,21], expected: {currentPage: 1, pageLength: 20, fullLength: 21, pages: 2, start: 1, end: 20}},
        {actual: [2,20,21], expected: {currentPage: 2, pageLength: 20, fullLength: 21, pages: 2, start: 20, end: 21}},
        {actual: [1,20,15], expected: {currentPage: 1, pageLength: 20, fullLength: 15, pages: 1, start: 1, end: 15}},
        {actual: [3,20,15], expected: {currentPage: 3, pageLength: 20, fullLength: 15, pages: 1, start: 0, end: 0}},
        {actual: [3,20,25], expected: {currentPage: 3, pageLength: 20, fullLength: 25, pages: 2, start: 0, end: 0}},
        {actual: [3,20,20], expected: {currentPage: 3, pageLength: 20, fullLength: 20, pages: 1, start: 0, end: 0}},
        {actual: [1,30,40], expected: {currentPage: 1, pageLength: 30, fullLength: 40, pages: 2, start: 1, end: 30}},
        {actual: [1,undefined,40], expected: {currentPage: 1, pageLength: 20, fullLength: 40, pages: 2, start: 1, end: 20}}
    ];

    var should = function(combination) {
        return 'should calculate pager with arguments currentPage: ' + combination[0] + ', pageLength: ' + combination[1] + ', fullLength: ' + combination[2];
    }

    angular.forEach(combinations, function(combination) {
        it(should(combination.actual), function() {
            inject(function(pagerFactory) {
                var pager = pagerFactory.newInstance(combination.actual[0],combination.actual[1],combination.actual[2]);
                expect(pager).toEqual(combination.expected);
            });
        });
    });

});

describe('MailOverviewCtrl', function() {
    var scope, $httpBackend;

    beforeEach(module('minigApp', 'fixture/maillist.json'));

    beforeEach(inject(function($rootScope, $controller, API_HOME, _$httpBackend_, _fixtureMaillist_){
       $httpBackend = _$httpBackend_;

       _$httpBackend_.whenGET(API_HOME+'message?folder=INBOX&page=1&page_length=20').respond(_fixtureMaillist_);

       scope = $rootScope.$new();

       $controller('MailOverviewCtrl', {$scope: scope});
    }));

    it('should have correct size', function() {
        $httpBackend.flush();
        expect(scope.data.mailList.length).toBe(20);
     //   expect(scope.pager).toEqual({currentPage: 1, pageLength: 20, fullLength: 45, pages: 3, start: 1, end: 20});
    });

});

describe("testing directive pagination", function() {

  var compile, scope;

  beforeEach(module('minigApp', 'fixture/maillist.json', 'htmlTemplates'));

  beforeEach(inject(function($compile, $rootScope, _fixtureMaillist_, templateService, $templateCache, $q) {
    compile = $compile;

    spyOn(templateService, 'template').andCallFake(function (params) {
        var deferred = $q.defer();
        deferred.resolve($templateCache.get('html/pagination.html'));
        return deferred.promise;
    });

    scope = $rootScope.$new();
    scope.data = _fixtureMaillist_;
  }));

  it("should calculate pager and append it to scope", function() {
    var element = compile('<pagination>')(scope);
    scope.$digest();

    expect(scope.pager).toEqual({currentPage: 1, fullLength: 45, pageLength: 20, start: 1, end: 20, pages: 3});
    //TODO test element
  });

});