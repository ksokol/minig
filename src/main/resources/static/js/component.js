angular.module("minigAppComponents", [])
.component('messageConversation', {
    bindings: {
        mail: '<'
    },
    templateUrl: 'templates/message-conversation.html',
    controller: ['$scope', function ($scope) {
        $scope.hasHtml = false;

        this.$onChanges = function(changesObj) {
            if (changesObj.mail.currentValue) {
                // TODO
                $scope.hasHtml = changesObj.mail.currentValue.html.length > 0;
                $scope.mail = changesObj.mail.currentValue;
            }
        }
    }]
})

.component('messageHtml', {
    bindings: {
        mail: '<'
    },
    template: '<iframe class="not-visible" frameborder="0" ng-src="{{url}}"></iframe>',
    controller: ['$scope', '$element', 'mailService', function($scope, $element, mailService) {
        this.$onChanges = function(changesObj) {
            if (changesObj.mail.currentValue) {
                $scope.url = mailService.htmlUrl(changesObj.mail.currentValue);
                var iframe = $element.find('iframe');

                iframe.on('load', function() {
                    var height = iframe[0].contentWindow.document.body.scrollHeight;
                    iframe.css('width', '100%');
                    iframe.css('height', height + 'px');
                    iframe.removeClass('not-visible');
                });
            }
        }
    }]
});
