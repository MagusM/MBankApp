(function(){
	var mbank = function($http) {
		
		var getActiveClient = function(formClient) {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/login";
			return $http.post(url, formClient)
				.then(function(response){
					return response.data;
				});
		};
		
		var useGet = function(url) {
			return $http.get(url)
				.then(function(response) {
					return response.data;
				});
		};
		
		return {
			getClient: getActiveClient,
			get: useGet
		};
	};
	
	var module = angular.module("mbankApp");
	module.factory("mbank", mbank);
	
}());