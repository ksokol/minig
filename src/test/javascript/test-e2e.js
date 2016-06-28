
describe('minigApp', function(){
     beforeEach(function() {
       browser().navigateTo('/miniG/login');
     });

    describe('login page', function() {
       it('should show login page', function() {
         expect(browser().location().url()).toBe("/miniG/login");
       });

    });
});