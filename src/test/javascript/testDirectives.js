beforeEach(module('minigApp', 'fixture/maillist.json', 'htmlTemplates'));

describe("directive pagination", function() {
    it("should calculate pager and append it to scope", inject(function($compile, $rootScope, _fixtureMaillist_) {
        scope = $rootScope.$new();

        $compile('<pagination data="data"></pagination>')(scope);

        scope.data = _fixtureMaillist_;
        scope.$digest();

        expect(scope.pager).toEqual({currentPage: 1, fullLength: 45, pageLength: 20, start: 21, end: 40, pages: 2});
        //TODO test element
    }));
});

describe("directive messageText", function() {
    it("should decode html entities", inject(function($compile, $rootScope) {
        scope = $rootScope.$new();
        scope.mail = {text: "&#39;"};
        $compile('<message-text></message-text>')(scope);
        scope.$digest();
        expect(scope.messageText).toEqual(["'"]);
    }));
});

describe("directive backLink", function() {

    var scope, routeService;

    beforeEach(module('minigApp', function($provide) {
        routeService = {
            previous: jasmine.createSpy('previous'),
            currentRoute: jasmine.createSpy('currentRoute')
        };

        routeService.previous.and.returnValue('/previousRoute');

        $provide.provider('routeService', function() {
            return {
                $get: function() {
                    return routeService;
                }
            };
        });

        $provide.value('i18nFilter', function() {
            return 'i18nFilter applied'
        });
    }));

    beforeEach(inject(function($rootScope) {
        scope = $rootScope.$new();
    }));

    it("should set previous route as href attribute", inject(function($compile) {
        var element = $compile('<back-link></back-link>')(scope);
        scope.$digest();

        expect(element.find('a')[0].getAttribute('href')).toEqual('#!/previousRoute');
    }));

    it("should localize link text", inject(function($compile) {
        var element = $compile('<back-link></back-link>')(scope);
        scope.$digest();

        expect(element.text()).toEqual('i18nFilter applied');
    }));

    it("should hide back link when current route is not /message", inject(function($compile) {
        var element = $compile('<back-link></back-link>')(scope);

        expect(element[0].getAttribute('style')).toEqual('display: none;');
    }));

    it("should show back link when current route is /message", inject(function($compile) {
        routeService.currentRoute.and.returnValue(true);
        var element = $compile('<back-link></back-link>')(scope);

        expect(element[0].getAttribute('style')).toEqual(null);
    }));

    it("should query for /message route", inject(function($compile) {
        $compile('<back-link></back-link>')(scope);

        expect(routeService.currentRoute).toHaveBeenCalledWith('message');
    }));
});
