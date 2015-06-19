(function(){
	var app = angular.module("mbankApp", []);

	var NavBarController = function ($scope, $location, $routeParams, mbank) {
		$scope.logout = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/logout";
			var logoutSuccess = function(response) {
				if (status == 201) {
					$rootScope.elementToShow = "homePage";
				}
			};
			var logoutError = function(reason) {
				
			};
			mbank.get(url).then(logoutSuccess, logoutError);
		};
	};

	app.controller("NavBarController", NavBarController);

}());