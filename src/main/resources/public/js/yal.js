var app = angular.module('yalApp', []).filter('timeformat', function() {
    //Returns duration from microseconds in hh:mm:ss.sss format.
      return function(microseconds) {
        var ms = 1000;
        var milliseconds = microseconds / ms;
        var s = 1000;
        var seconds = Math.floor(milliseconds / s);
        var h = 3600;
        var m = 60;
        var hours = Math.floor(seconds/h);
        var minutes = Math.floor( (seconds % h)/m );
        var seconds = Math.floor( (seconds % m) );
        var millis = Math.floor(milliseconds - (seconds * s) - (minutes * s * m) - (hours * s * m * h));
        var timeString = '';
        if(millis < 10) millis = "00"+millis;
        else if(millis < 100) millis = "0"+millis;
        if(seconds < 10) seconds = "0"+seconds;
        if(hours < 10) hours = "0"+hours;
        if(minutes < 10) minutes = "0"+minutes;
        timeString = hours +":"+ minutes +":"+seconds + "." + millis;
        return timeString;
    }
});

app.controller('yalCtrl', function($scope, $http, $interval) {
	var socket = new WebSocket("ws://localhost:4567/updates");
	socket.onmessage = function (e) {
	  console.log('Server: ' + e.data);
	  var event = angular.fromJson(e.data);
	  if(event.eventType == 'ChannelUpdated'){
	  	$scope.channels[event.channel.id] = event.channel;
	  } else if(event.eventType == 'LoopCreated'){
	    $scope.loops[event.loop.id] = event.loop;
	    updateCurrentLoop();	
	  } else if(event.eventType == 'LoopUpdated'){
	    $scope.loops[event.loop.id] = event.loop;
	    updateCurrentLoop();	
	  } else if(event.eventType == 'LooperStateUpdated'){
	    $scope.looperState = event.looperState;
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
	$scope.pause = function() {
		$http({
			method : 'GET',
			url : '/pause'
		});
	};
	$scope.stop = function() {
		$http({
			method : 'GET',
			url : '/stop'
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
	$scope.sampleMute = function(loopId, sampleId, mute) {
		$http({
			method : 'GET',
			url : '/sample/' + loopId + '/' + sampleId + '/' + mute
		});
	};
	$scope.playSample = function(loopId, sampleId) {
		$http({
			method : 'GET',
			url : '/sample/play/' + loopId + '/' + sampleId
		});
	};
	$scope.test = function(){
		socket.send("test");
	};
	$scope.save = function(){
		$http({
			method: 'GET',
			url: '/save'
		});
	};
	$scope.updateAudioSettings = function(){
		$scope.settings.audioSettings = angular.fromJson($scope.selectedAudioSettings);
		$http({
			method : 'POST',
			url : '/settings/new',
			data: $scope.settings
		});
	};
	
	updateSettings();
	updateAvailableAudioSettings();
	
	updateChannels();
	updateLoops();
	
	updateLooperState();
	
	function updateSettings(){
		$http({
			method : 'GET',
			url : '/settings'
		}).success(function(data, status, headers, config) {
			$scope.settings = data;
		}).error(function(data, status, headers, config) {
		});
	}
	
	function updateAvailableAudioSettings(){
		$http({
			method : 'GET',
			url : '/settings/available/audio'
		}).success(function(data, status, headers, config) {
			$scope.availableAudioSettings = data;
		}).error(function(data, status, headers, config) {
		});
	}
	
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
	function updateLooperState(){
		$http({
			method : 'GET',
			url : '/looperstate'
		}).success(function(data, status, headers, config) {
			$scope.looperState = data;
		}).error(function(data, status, headers, config) {
		});
	}
	
	function updateLoops(){
		$http({
			method : 'GET',
			url : '/loops'
		}).success(function(data, status, headers, config) {
			$scope.loops = [];
			angular.forEach(data, function(value, key){
				$scope.loops[value.id] = value;
			});
			updateCurrentLoop();			
		}).error(function(data, status, headers, config) {
		});
	}
	
	function updateCurrentLoop(){
		var activeLoops = $scope.loops.filter(function(n){ return n.active === true; });
		if(activeLoops.length == 1){
			$scope.currentLoop = activeLoops[0];
			$scope.currentLoop.position = 0;
		}
	}
});