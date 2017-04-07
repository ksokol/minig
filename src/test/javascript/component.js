describe('Component: messageConversation', function () {

    var scope, el;

    beforeEach(module('minigAppComponents', 'htmlTemplates'));

    beforeEach(inject(function($compile, $rootScope) {
        scope = $rootScope.$new();

        el = $compile('<message-conversation mail="mail"></message-conversation>')(scope);
        scope.$apply();
    }));

    it('should activate messagePlain component when html is not available', function() {
        expect(el.find('message-html')[0]).toBeUndefined();
        expect(el.find('message-text')[0]).toBeDefined();
    });

    it('should activate messageHtml component when html is available', function() {
        scope.mail = {id: 'folder|messageId', html: ''};
        scope.$apply();

        expect(el.find('message-html')[0]).toBeUndefined();
        expect(el.find('message-text')[0]).toBeDefined();
    });

    it('should activate messagePlain component when mail is not available', function() {
        scope.mail = {id: 'folder|messageId', html: 'html'};
        scope.$apply();

        expect(el.find('message-html')[0]).toBeDefined();
        expect(el.find('message-text')[0]).toBeUndefined();
    });
});

describe('Component: messageHtml', function () {

    describe('attributes', function() {

        var scope, el;

        beforeEach(module('minigAppComponents', 'htmlTemplates'));

        beforeEach(inject(function($compile, $rootScope) {
            scope = $rootScope.$new();

            el = $compile('<message-html mail="mail"></message-html>')(scope);
        }));

        it('should build src attribute', function () {
            scope.mail = {id: 'folder|messageId'};
            scope.$digest();

            expect(el.find('iframe').attr('src')).toBe('api/1/message/folder|messageId/html');
        });

        it('should hide frame border', function () {
            expect(el.find('iframe').attr('frameborder')).toBe('0');
        });
    });

    describe('events', function() {

        var $componentController, el, iframe, loadCallback;

        var mailService = {
            htmlUrl: function() { return 'http://localhost/irrelevant' }
        };

        beforeEach(module('minigAppComponents', function() {
            iframe = {
                on: function(type, fn) {
                    loadCallback = fn;
                },
                0: {
                    contentWindow: {
                        document: {
                            body: {
                                scrollHeight: 200
                            }
                        }
                    }
                },
                css: jasmine.createSpy('iframe.css'),
                removeClass: jasmine.createSpy('iframe.removeClass')
            };

            el = {
                find: function() { return iframe }
            };

            spyOn(iframe, 'on').and.callThrough();
            spyOn(el, 'find').and.callThrough();
        }));

        beforeEach(inject(function(_$componentController_) {
            $componentController = _$componentController_;
        }));

        it('should subscribe to load function on iframe', function() {
            $componentController('messageHtml', {$element: el, mailService: mailService})
                .$onChanges({ mail: { currentValue: { id: 1}}});

            expect(el.find).toHaveBeenCalledWith('iframe');
            expect(iframe.on).toHaveBeenCalledWith('load', jasmine.any(Function));
        });

        it('should resize and show iframe after load', function() {
            $componentController('messageHtml', {$element: el, mailService: mailService})
                 .$onChanges({ mail: { currentValue: { id: 1}}});

            loadCallback();

            expect(iframe.css.calls.allArgs()).toEqual([['width', '100%'], ['height', '200px']]);
            expect(iframe.removeClass).toHaveBeenCalledWith('not-visible');
        });

        it('should not subscribe to load function on iframe when currentValue is undefined', function() {
            $componentController('messageHtml', {$element: {}})
                .$onChanges({ mail: { currentValue: undefined }});

            expect(el.find).not.toHaveBeenCalledWith('iframe');
            expect(iframe.on).not.toHaveBeenCalledWith('load', jasmine.any(Function));
        });
    });
});
