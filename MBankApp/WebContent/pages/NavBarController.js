(function(){
	var app = angular.module("app");

	var NavBarController = function ($scope, $location, mbank) {
		$scope.logout = function() {
			console.log("trying to logout");
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/logout";
			var logoutSuccess = function(response) {
				if (status == 200) {
					$location.path("/main");
				}
			};
			var logoutError = function(reason) {
				console.log("Logut Failed");
			};
			mbank.reachMBank(url).then(logoutSuccess, logoutError);
		};
		$scope.showAdminPanel = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/adminPanel";
			var onAdminPanelSuccess = function(response){
			};
			var onAdminPanelError = function(reason){
				console.log("Failed to start admin panel");
			};
			mbank.reachMBank(url).then(onAdminPanelSuccess, onAdminPanelError);
		};
		$scope.logout = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/logout";
			var onLogoutSuccess = function(response){
				$location.path("/main");
			};
			var onlogoutError = function(reason){
				console.log("Failed to logout");
			};
			mbank.reachMBank(url).then(onLogoutSuccess, onlogoutError);
		};
		
		$scope.lol = function() {
			alert("You Don't Have A Choice, LOL!!!");
		};
	};

	app.controller("NavBarController", NavBarController);

}());