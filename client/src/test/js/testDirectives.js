beforeEach(module('minigApp', 'fixture/maillist.json', 'htmlTemplates'));

describe("directive pagination", function() {
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

describe("directive messageText", function() {
    it("should decode html entities", inject(function($compile, $rootScope) {
        scope = $rootScope.$new();
        scope.mail = {body: {plain: "&#39;"}};
        $compile('<message-text></message-text>')(scope);
        scope.$digest();
        expect(scope.messageText).toEqual(["'"]);
    }));
});