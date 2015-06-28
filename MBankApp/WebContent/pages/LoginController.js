(function(){
	var app = angular.module("app");

	var LoginController = function ($scope, $location, mbank) {
		$scope.checkId = function (isIdValid) {
			$scope.loginError_show = false;
			if (!isIdValid) {
				$scope.loginError_show = true;
			}
			else {
				$scope.loginError_msg = "Please Enter Valid Client Details";
				$scope.loginError_show = false;

			};
		};

		$scope.login = function (client) {
			var confirmedClient = null;
			$scope.loginError_show = false;
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/login";
			var loginPostComplete = function(response) {
				$scope.loginError_show = false;
				if (response.status == 409 || response.status == 204) {
					$scope.loginError_show = true;
					$scope.loginError_msg = "Please Enter Valid Client Details";
				}if (response.status == 200) {
					confirmedClient = response.data;
					if (confirmedClient.comment == null) {confirmedClient.comment = "No Comment";}
					$location.path("/client/" + confirmedClient.name);
				}
			};
			var loginPostError = function(reason) {
				$scope.loginError_show = false;
				$scope.loginError_show = true;
				$scope.loginError_msg = "Please Enter Valid Client Details";
			};
			mbank.sendDataToMbank(url, client).then(loginPostComplete, loginPostError);
		};
	};

	app.controller("LoginController", LoginController);

}());