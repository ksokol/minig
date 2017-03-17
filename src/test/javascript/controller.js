describe('MessageCtrl', function() {
    var scope, rootScope, routeParams, mailService, httpBackend, apiHome, controller, q, firstDeferred, routeService, i18nService;
    var composerService = {};

    beforeEach(module('minigApp', 'htmlTemplates'));

    beforeEach(inject(function($rootScope, $controller, API_HOME, _$httpBackend_, $q){
        httpBackend = _$httpBackend_;
        apiHome = API_HOME;
        q = $q;
        controller = $controller;

        rootScope = {
          $broadcast: jasmine.createSpy('$broadcast')
        };

        mailService = {
            load: jasmine.createSpy('load'),
            updateFlags: jasmine.createSpy('updateFlags')
        };

        routeService = {
            navigateTo: jasmine.createSpy('navigateTo')
        };

        i18nService = {
            resolve: jasmine.createSpy('resolve')
        };

        scope = $rootScope.$new();
        firstDeferred = $q.defer();

        routeParams = {
          id: 'INBOX|<1>'
        };
    }));

    describe('on init', function() {

        var services;

        beforeEach(function() {
            services = {
                $scope: scope, $routeParams: routeParams, mailService: mailService, composerService: composerService,
                $rootScope: rootScope,
                routeService: routeService,
                i18nService: i18nService
            };
        });

        it('should not mark message as read when message is already read', function() {
            firstDeferred.resolve({read: true});
            mailService.load.and.returnValue(firstDeferred.promise);

            controller('MessageCtrl', services);
            scope.$apply();

            expect(mailService.updateFlags).not.toHaveBeenCalled();
        });

        it('should mark message as read when message is not read', function() {
            firstDeferred.resolve({id: 1, read: false});
            mailService.load.and.returnValue(firstDeferred.promise);

            var deferred2 = q.defer();
            deferred2.resolve({});
            mailService.updateFlags.and.returnValue(deferred2.promise);

            controller('MessageCtrl', services);
            scope.$apply();

            expect(mailService.updateFlags).toHaveBeenCalledWith([{id: 1, read: true}]);
            expect(rootScope.$broadcast).toHaveBeenCalledWith('more-actions-done');
        });

        it('should broadcast an error and redirect to box when load returned an error', function() {
            firstDeferred.reject({});
            mailService.load.and.returnValue(firstDeferred.promise);
            i18nService.resolve.and.returnValue('expected');

            controller('MessageCtrl', services);
            scope.$apply();

            expect(rootScope.$broadcast).toHaveBeenCalledWith('error', 'expected');
            expect(routeService.navigateTo).toHaveBeenCalledWith('box');
        });

        it('should broadcast error and redirect to box when updateFlags returned an error', function() {
            firstDeferred.resolve({id: 1, read: false});
            mailService.load.and.returnValue(firstDeferred.promise);

            var secondDeferred = q.defer();
            secondDeferred.reject({});
            mailService.updateFlags.and.returnValue(secondDeferred.promise);

            i18nService.resolve.and.returnValue('expected');

            controller('MessageCtrl', services);
            scope.$apply();

            expect(rootScope.$broadcast.calls.argsFor(0)).toEqual(['more-actions-done']);
            expect(rootScope.$broadcast.calls.argsFor(1)).toEqual(['error', 'expected']);
        });
    });
});
