(function(){
	var myMBankApp = angular.module("mbankApp", []);
	/*
	 * navbar controller
	 */
	var navBarCtrl = function($http, $scope, $rootScope) {
		$scope.logout = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/logout";
			var logoutSuccess = function(response) {
				if (status == 201) {
					$rootScope.elementToShow = "homePage";
				}
			};
			var logoutError = function(reason) {

			};
			$http.get(url).then(logoutSuccess, logoutError);


			var res = $http.get(url);
		};

		$scope.showHomePage = function () {
			$rootScope.elementToShow = "homePage";
		};
		$scope.showLoginPage = function () {
			$rootScope.elementToShow = "loginPage";
		};
		$scope.showClientPage = function () {
			$rootScope.elementToShow = "clientPage";
		};
	};

	/*
	 * homepage controller
	 */
	var homePageCtrl = function($http, $scope, $rootScope) {

	};

	/*
	 * login controller
	 */
	var loginCtrl = function($http, $scope, $rootScope) {
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
			$scope.loginError_show = false;
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/login";
			var loginPostComplete = function(response) {
				$scope.loginError_show = false;
				if (response.status == 204) {
					$scope.loginError_show = true;
					$scope.loginError_msg = "Please Enter Valid Client Details";
				}if (response.status == 201) {
					$rootScope.elementToShow = "clientPage";
				}

			};
			var loginPostError = function(reason) {
				$scope.loginError_show = false;
				$scope.loginError_show = true;
				$scope.loginError_msg = "Please Enter Valid Client Details";
			};
			$http.post(url,client).then(loginPostComplete, loginPostError);
		};
	};

	/*
	 * client action controller
	 */
	var clientActionCtrl = function($scope, $rootScope, $http) {
		var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/summaryDetails";
		var onSummaryReqComplete = function(response){
			console.log(response.data);
			$scope.summary = response.data;
		};
		var onSummaryReqError = function(reason){

		}
		$http.get(url).then(onSummaryReqComplete, onSummaryReqError);
	};


	/*
	 * setting controllers to module
	 */
	myMBankApp.controller("navBarCtrl", navBarCtrl);
	myMBankApp.controller("homePageCtrl",homePageCtrl);
	myMBankApp.controller("loginCtrl", loginCtrl);
	myMBankApp.controller("clientActionCtrl", clientActionCtrl);

}());


/*
 * $cahce for storing user data info
 * catch after then
 */






//var myMBankApp = angular.module('mbankApp', []);

///*
//* NavBar Controller
//*/
//myMBankApp.controller("navBarCtrl", function($http, $scope, $rootScope) {
//$scope.logout = function() {
//var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/logout"
//var res = $http.get(url);
//res.success(function(data, status) {
//if (status == 201) {
//$rootScope.elementToShow = "homePage";
//}
//});
//};

//$scope.showHomePage = function () {
//$rootScope.elementToShow = "homePage";
//};
//$scope.showLoginPage = function () {
//$rootScope.elementToShow = "loginPage";
//};
//$scope.showClientPage = function () {
//$rootScope.elementToShow = "clientPage";
//};
//});

//myMBankApp.controller("homePageCtrl" , function($http, $scope, $rootScope) {

//});


///*
//* Login Controller
//*/
//myMBankApp.controller("loginCtrl", function($scope, $rootScope, $http) {
//$scope.checkId = function (isIdValid) {
//$scope.loginError_show = false;
//if (!isIdValid) {
//$scope.loginError_show = true;
//}
//else {
//$scope.loginError_msg = "Please Enter Valid Client Details";
//$scope.loginError_show = false;
//}
//};

//$scope.login = function (client) {
//$scope.loginError_show = false;
////var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/login?id="+client.id+"&password="+client.password+"&email="+client.email;
//var url2 = "http://localhost:8080/MBankApp/MBank/MBankRoot/login";
//var res = $http.post(url2, client);
//res.success(function(data, status) {
//$scope.loginError_show = false;
//if (status == 204) {
//$scope.loginError_show = true;
//$scope.loginError_msg = "Please Enter Valid Client Details";
//}if (status == 201) {
//$rootScope.elementToShow = "clientPage";
//}
//});
//res.error(function(){
//$scope.loginError_show = false;
//$scope.loginError_show = true;
//$scope.loginError_msg = "Please Enter Valid Client Details";
//});
//};
//});

//myMBankApp.controller("clientActionCtrl", function($scope, $rootScope, $http) {
//var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/getSummaryDetails";
//ver res = $http.get(url);
//res.success(function(data){
//$scope.summary = data;
//});
//res.error(function(){

//});
//});


////myMBankApp.controller("clientActionCtrl", function($scope, $rootScope, $http) {
////var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/getSummaryDetails";
////var onSummaryReqComplete = function(response) {
////$scope.summary = response.data;
////};
////var onSummaryError = function(reason) {
////$scope.summary = {"account_balance" : "Temporary Error", "deposits_balance" : "Temporary Error", "account_type" : "Temporary Error", "credit_limit" : "Temporary Error"};
////};
////$http.get(url).then(onSummaryReqComplete, onSummaryError);
////});


