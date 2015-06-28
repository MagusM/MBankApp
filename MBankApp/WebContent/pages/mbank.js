(function(){
	var mbank = function($http) {
		
		var postToMBank = function(url, data) {
			return $http.post(url, data)
				.then(function(response){
					return response;
				});
		};
		
		var useGet = function(url) {
			return $http.get(url)
				.then(function(response) {
					return response;
				});
		};
		
		return {
			sendDataToMbank: postToMBank,
			reachMBank: useGet
		};
	};
	
	var module = angular.module("app");
	module.factory("mbank", mbank);
	
}());