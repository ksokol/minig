
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
        _$httpBackend_.whenGET('box.html').respond("box.html");

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

    beforeEach(module('minigApp', 'fixture/maillist.json', 'htmlTemplates'));

    beforeEach(inject(function($rootScope, $controller, API_HOME, _$httpBackend_, _fixtureMaillist_){
        $httpBackend = _$httpBackend_;

        _$httpBackend_.whenGET(API_HOME+'message?folder=INBOX&page=1&page_length=20').respond(_fixtureMaillist_);
        _$httpBackend_.whenGET('box.html').respond("box.html");

        scope = $rootScope.$new();

        $controller('MailOverviewCtrl', {$scope: scope});
    }));

    it('should have correct size', function() {
        $httpBackend.flush();
        expect(scope.data.mailList.length).toBe(20);
    });

});

describe("testing directive pagination", function() {

    beforeEach(module('minigApp', 'fixture/maillist.json', 'htmlTemplates'));

    it("should calculate pager and append it to scope", inject(function($compile, $rootScope, _fixtureMaillist_, _$httpBackend_) {
        _$httpBackend_.whenGET('pagination.html').respond("pagination.html");
        scope = $rootScope.$new();

        var element = $compile('<pagination data="data"></pagination>')(scope);
        _$httpBackend_.flush();

        scope.data = _fixtureMaillist_;
        scope.$digest();

        expect(scope.pager).toEqual({currentPage: 1, fullLength: 45, pageLength: 20, start: 1, end: 20, pages: 3});
        //TODO test element
    }));

});

describe('folderCache', function(){
    var folder = {};
    var folder0 = {"id":"INBOX"};
    var folder1 = {"id":"INBOX/a1"};
    var folder2 = {"id":"INBOX/a2"};
    var folder3 = {"id":"INBOX/a10"};

    beforeEach(module('minigApp'));

    it("shouldn't be empty after inserting a folder", function() {
        inject(function(folderCache) {
            expect(folderCache.isEmpty()).toBe(true);
            folderCache.add(folder);
            expect(folderCache.isEmpty()).toBe(false);
        });
    });

    it("should be sorted after subsequent adds", function() {
        inject(function(folderCache) {

            folderCache.add(folder0);
            folderCache.add(folder3);
            folderCache.add(folder2);
            folderCache.add(folder1);

            expect(folderCache.snapshot()).toEqual([folder0, folder1, folder3, folder2]);
        });
    });

    it("snapshot should be sorted", function() {
        inject(function(folderCache) {
            folderCache.fill([folder0, folder3, folder2, folder1]);
            folderCache.clear();
            expect(folderCache.snapshot()).toEqual([]);
        });
    });

    it("should evict element", function() {
        inject(function(folderCache) {
            folderCache.fill([folder0, folder3, folder2, folder1]);
            folderCache.evict(folder2.id);
            expect(folderCache.snapshot()).toEqual([folder0, folder3, folder1]);
        });
    });

});

describe('mailCache', function() {
    var mail1 = {id : "1"};
    var mail2 = {id : "2"};

    beforeEach(module('minigApp'));

    beforeEach(inject(function(mailCache) {
        sut = mailCache;
    }));


    it("should have size of 2", function() {
        expect(sut.size()).toBe(0);
        sut.add(mail1);
        expect(sut.size()).toBe(1);
        sut.add(mail2);
        expect(sut.size()).toBe(2);
    });

    it("should have size of 1", function() {
        expect(sut.size()).toBe(0);
        sut.add(mail1);
        expect(sut.size()).toBe(1);
        sut.add(mail1);
        expect(sut.size()).toBe(1);
    });

    it("should evict cache entries", function() {
        expect(sut.size()).toBe(0);
        sut.add(mail1);
        expect(sut.size()).toBe(1);
        sut.add(mail2);
        expect(sut.size()).toBe(2);
        sut.evict(mail1.id);
        expect(sut.size()).toBe(1);
        expect(sut.get(mail2.id)).toEqual(mail2);
    });
});

describe('htmlConversion', function() {
    beforeEach(module('minigApp'));

    beforeEach(inject(function(htmlConversion) {
        sut = htmlConversion;
    }));

    it("should be empty string", function() {
        var resultEmpty = sut.convertToPlain("");
        expect(resultEmpty).toEqual("");

        var resultEmpty2 = sut.convertToPlain(null);
        expect(resultEmpty2).toEqual("");
    });

    it("should be bold string", function() {
        var resultEmpty = sut.convertToPlain("<b>bold</b>");
        expect(resultEmpty).toEqual("**bold**");
    });

    it("should be plain string", function() {
        var resultEmpty = sut.convertToPlain("<div>plain</div>");
        expect(resultEmpty).toEqual("plain");
    });

    it("should be quoted string", function() {
        var resultEmpty = sut.convertToPlain("<blockquote>quote</blockquote>");
        expect(resultEmpty).toEqual("> quote");
    });
});