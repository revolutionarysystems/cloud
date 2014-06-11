var dashboard = angular.module('dashboard', []);

dashboard.controller('DashboardCtrl', function($scope, $http) {
	$http({
		method: 'GET',
		url: 'http://localhost:8080/cloud-service/'
	}).
	success(function(data, status, headers, config) {
		$scope.instances = data;
	}).
	error(function(data, status, headers, config) {
		alert("Failed to communicate with the server");
	});

	$scope.startInstance = function(id) {
		console.log("Starting instance: " + id);
		$http({
			method: 'GET',
			url: 'http://localhost:8080/cloud-service/' + encodeURIComponent(id.replace("/", "|")) + "/start"
		}).
		success(function(data, status, headers, config) {
			$scope.instances = data;
		}).
		error(function(data, status, headers, config) {
			alert("Failed to communicate with the server");
		});
	}

	$scope.stopInstance = function(id) {
		console.log("Stopping instance: " + id);
		$http({
			method: 'GET',
			url: 'http://localhost:8080/cloud-service/' + encodeURIComponent(id.replace("/", "|")) + "/stop"
		}).
		success(function(data, status, headers, config) {
			$scope.instances = data;
		}).
		error(function(data, status, headers, config) {
			alert("Failed to communicate with the server");
		});
	}
});