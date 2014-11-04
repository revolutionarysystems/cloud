var dashboard = angular.module('dashboard', []);

dashboard.controller('DashboardCtrl', function($scope, $http) {
	sendRequest("");

	$http({
		method: 'GET',
		url: cloudServiceUrl + "image"
	}).
	success(function(data, status, headers, config) {
		$scope.images = data;
	}).
	error(function(data, status, headers, config) {
		alert("Failed to communicate with the server");
	});

	$scope.startInstance = function(id) {
		console.log("Starting instance: " + id);
		sendRequest(encodeURIComponent(id.replace("/", "|")) + "/start");
	}

	$scope.stopInstance = function(id) {
		console.log("Stopping instance: " + id);
		sendRequest(encodeURIComponent(id.replace("/", "|")) + "/stop");
	}

	$scope.terminateInstance = function(id) {
		console.log("Terminate instance: " + id);
		sendRequest(encodeURIComponent(id.replace("/", "|")) + "/terminate");
	}

	$scope.getImageInstances = function(image){
		var result = [];
		for(var i in $scope.instances){
			var instance = $scope.instances[i];
			if(instance.name == image.name){
				result.push(instance);
			}
		}
		return result;
	}

	$scope.getImageStatus = function(image){
		if(!$scope.instances){
			return "";
		}
		var instances = $scope.getImageInstances(image);
		if(image.ipAddress == null){
			return instances.length + " instances running";
		}else{
			if(instances.length == 0){
				return "SUSPENDED";
			}else{
				return instances[0].status;
			}
		}
	}

	$scope.canBeStopped = function(instance){
		var spotInstance = (instance.userMetadata.SpotInstance == "true");
		return instance.status == "RUNNING" && !spotInstance;
	}

	$scope.canBeStarted = function(image){
		if(!$scope.instances){
			return false;
		}
		return image.ipAddress == null || $scope.getImageInstances(image).length == 0;
	}

	$scope.canBeTerminated = function(instance){
		var spotInstance = (instance.userMetadata.SpotInstance == "true");
		return instance.status == "RUNNING" && spotInstance;
	}

	$scope.startImage = function(name){
		$http({
			method: 'GET',
			url: cloudServiceUrl + "image/" + name + "/start"
		}).
		success(function(data, status, headers, config) {
			sendRequest("");
		}).
		error(function(data, status, headers, config) {
			alert("Failed to communicate with the server");
		});
	}

	function sendRequest(path) {
		$http({
			method: 'GET',
			url: cloudServiceUrl + path
		}).
		success(function(data, status, headers, config) {
			$scope.instances = data;
		}).
		error(function(data, status, headers, config) {
			alert("Failed to communicate with the server");
		});
	}
});