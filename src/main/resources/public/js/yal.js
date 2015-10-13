var app = angular.module('yalApp', []);

app.controller('yalCtrl', function($scope, $http, $interval) {
	var socket = new WebSocket("ws://localhost:4567/updates");
	socket.onmessage = function (e) {
	  console.log('Server: ' + e.data);
	  var event = angular.fromJson(e.data);
	  if(event.eventType == 'ChannelUpdated'){
	  	$scope.channels[event.channel.id] = event.channel;
	  } else if(event.eventType == 'LoopCreated'){
	    $scope.loops[event.loop.id] = event.loop;
	  } else if(event.eventType == 'LoopUpdated'){
	    $scope.loops[event.loop.id] = event.loop;
	  } else if(event.eventType == 'SampleCreated'){
	    $scope.samples[event.sample.id] = event.sample;
	  } else if(event.eventType == 'SampleUpdated'){
	    $scope.samples[event.sample.id] = event.sample;
	  }
	};

	$scope.activateLoop = function(loopId) {
		$http({
			method : 'PUT',
			url : '/activateLoop/' + loopId
		});
	};

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
	$scope.record = function(channelId, enabled) {
		$http({
			method : 'GET',
			url : '/record/' + channelId + '/' + enabled
		});
	};
	$scope.monitor = function(channelId, enabled) {
		$http({
			method : 'GET',
			url : '/monitor/' + channelId + '/' + enabled
		});
	};
	$scope.sampleMute = function(sampleId, mute) {
		$http({
			method : 'GET',
			url : '/sample/' + sampleId + '/' + mute
		});
	};
	$scope.playSample = function(sampleId) {
		$http({
			method : 'GET',
			url : '/sample/play/' + sampleId
		});
	};
	$scope.test = function(){
		socket.send("test");
	};
	
	
	updateChannels();
	updateLoops();
	
	function updateChannels(){
		$http({
			method : 'GET',
			url : '/channels'
		}).success(function(data, status, headers, config) {
			$scope.channels = data;
		}).error(function(data, status, headers, config) {
		});
	}
	
	function updateLoopLength(){
		$http({
			method : 'GET',
			url : '/length'
		}).success(function(data, status, headers, config) {
			$scope.loopLength = data;
		}).error(function(data, status, headers, config) {
		});
	}
	
	function updateLoops(){
		$http({
			method : 'GET',
			url : '/loops'
		}).success(function(data, status, headers, config) {
			$scope.loops = data;
			$scope.currentLoop = data.filter(function(n){ return n.active === true; })[0];
		}).error(function(data, status, headers, config) {
		});
	}
});