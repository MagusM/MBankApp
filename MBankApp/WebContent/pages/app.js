(function(){
	var app = angular.module("app", ["ngRoute"]);
	var routeProvider = function($routeProvider){
		$routeProvider
		.when("/main", {
			templateUrl: "main.html",
			controller: "MainController"
		})
		.when("/login", {
			templateUrl: "login.html",
			controller: "LoginController"
		})
		.when("/client/:client", {
			templateUrl: "client.html",
			controller: "ClientController"
		})
		.otherwise({redirectTo:"/main"});
	};
	app.config(routeProvider);
}());