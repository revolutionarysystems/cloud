var dashboard = angular.module('dashboard', []);

dashboard.controller('DashboardCtrl', function($scope, $http) {
	sendRequest("");

	$scope.startInstance = function(id) {
		console.log("Starting instance: " + id);
		sendRequest(encodeURIComponent(id.replace("/", "|")) + "/start");
	}

	$scope.stopInstance = function(id) {
		console.log("Stopping instance: " + id);
		sendRequest(encodeURIComponent(id.replace("/", "|")) + "/stop");
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