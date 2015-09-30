var app = angular.module('yalApp', []);
app.controller('yalCtrl', function($scope, $http) {
    $scope.play = function(){
    	$http({
    		  method: 'GET',
    		  url: '/play'
    		});
    };
    $scope.loop = function(){
    	$http({
  		  method: 'GET',
  		  url: '/loop'
  		});
    };
});