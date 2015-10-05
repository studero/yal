var app = angular.module('yalApp', []);

app.controller('yalCtrl', function($scope, $http, $interval) {
	var socket = new WebSocket("ws://localhost:4567/updates");
	socket.onmessage = function (e) {
	  console.log('Server: ' + e.data);
	  var event = angular.fromJson(e.data);
	  if(event.eventType == 'ChannelStateChanged'){
	  	$scope.channels[event.id].recordingState = event.recordingState;
	  	$scope.channels[event.id].monitoring = event.monitoring;
	  } else if(event.eventType == 'LoopLengthChanged'){
	    $scope.loopLength = event.loopLength;
	    $scope.loopLocation = $scope.loopPosition + ' / ' + $scope.loopLength;
	  } else if(event.eventType == 'LoopPositionChanged'){
	    $scope.loopPosition = event.loopLength;
	    $scope.loopLocation = $scope.loopPosition + ' / ' + $scope.loopLength;
	  } else if(event.eventType == 'ChannelMonitorValueChanged'){
	    var meterLevel = event.value * 1000;
	    if(meterLevel > 200) meterLevel = 200;
	    $scope.channels[event.id].meterLevel = meterLevel;
	    $("#meter_" + event.id).css("height", meterLevel);
	    console.log('meterLevel: ' + meterLevel);
	  }
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
	
	function updateLoopLength(){
		$http({
			method : 'GET',
			url : '/length'
		}).success(function(data, status, headers, config) {
			$scope.loopLength = data;
		}).error(function(data, status, headers, config) {
		});
	}
});