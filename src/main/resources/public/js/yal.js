var app = angular.module('yalApp', []);
app.controller('yalCtrl', function($scope, $http, $interval) {
	$scope.play = function() {
		$http({
			method : 'GET',
			url : '/play'
		});
	};
	$scope.loop = function() {
		$http({
			method : 'GET',
			url : '/loop'
		});
	};
	
	$interval(updateChannels, 1000);
	$interval(updateSamples, 1000);
	
	function updateChannels(){
		$http({
			method : 'GET',
			url : '/channels'
		}).success(function(data, status, headers, config) {
			$scope.channels = data;
		}).error(function(data, status, headers, config) {
		});
	}
	
	function updateSamples(){
		$http({
			method : 'GET',
			url : '/samples'
		}).success(function(data, status, headers, config) {
			$scope.samples = data;
		}).error(function(data, status, headers, config) {
		});
	}
});