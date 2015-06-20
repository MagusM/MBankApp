(function(){
	var app = angular.module("mbankApp",[]);
	var FooCtrl = function($scope,$http) {
		$scope.login = function(){
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/login";
			var newClient;
			var loginPostComplete = function(response) {
				if (response.status == 409 || response.status == 204) {
					alert("Please Enter Valid Client Details");
				}if (response.status == 200) {
					newClient = response.data;
					if (newClient.comment == null) {
						newClient.comment = "Null";
					}
					$scope.bar = newClient;
				}
			};
			var loginPostError = function(reason) {
				alert("loginPostError");
			};
			var client = {
					"id"	  : 103,
					"email"	  : "simon@mail.com",
					"password": "mor"
			};
			$scope.orgClient = client.id;
			$http.post(url, client).then(loginPostComplete, loginPostError);
		};
		$scope.logout = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/logout";
			$http.get(url).then(function(response){
				if (response.status == 201) {
					newClient = null;
					$scope.summary = null;
					$scope.bar = newClient;
				}
			});
		};
		$scope.showSummary = function(){
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/summaryDetails";
			$http.get(url).then(function(response){
				if (response.status == 200) {
					var summaryToReturn = response.data;
					if (summaryToReturn.creditLimit = 1.7976931348623157E308) {
						summaryToReturn.creditLimit = "Unlimited";
						$scope.summary = summaryToReturn;
					}
				}
			});
		};
		$scope.adminPanel = function(){
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/adminPanel";
			$http.get(url);
		};
		
		
		
	};
	app.controller("FooCtrl", FooCtrl);
}());