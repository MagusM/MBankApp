(function(){
	var app = angular.module("app");

	var ClientController = function ($scope, $interval ,$routeParams, $location, mbank) {
		/*
		 * defines an optional fixed alert error and redierct to main
		 */
		var redirectToHomePage = function(alertErrorAsString){
			if (alertErrorAsString == null) {
				alertErrorAsString = "You were logged out, please Login again to continue.";
			}
			alert(alertErrorAsString);
			$location.path("/main");
		};
		$scope.name = $routeParams.client;
		/*
		 * for displaying summary
		 */
		var getSummaryForClient = function(){
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/summaryDetails";
			var onSummaryReqComplete = function(response){
				if (response.status == 200) {
					var summaryToReturn = response.data;
					if (summaryToReturn.creditLimit == 1.7976931348623157E308) {
						summaryToReturn.creditLimit = "Unlimited";
						var accountBalance = parseInt(summaryToReturn.accountBalance);
						summaryToReturn.accountBalance = accountBalance.toFixed(2);
						$scope.summary = summaryToReturn;
					}
				}
			};
			var onSummaryReqError = function(reason) {
				redirectToHomePage();
				
			};
			mbank.reachMBank(url).then(onSummaryReqComplete, onSummaryReqError);
		};
		var showSummary = function() {
			$interval(getSummaryForClient, 1, 1);
		};
		showSummary();
		/*
		 * changing view of client actions
		 */
		$scope.showUpdateClientDiv = function(){
			$scope.divToShow = "showUodateClient";
		};
		$scope.showViewAccountDiv = function(){
			viewAccountDetails();
			$scope.divToShow = "showViewAccount";
		};
		$scope.showViewDepositsDiv = function(){
			viewDepositsDetails();
			$scope.divToShow = "showViewDeposits";
		};
		$scope.showViewDepositToAccountDiv = function(){
			$scope.divToShow = "showDeposit";
		};
		$scope.showViewWithdrawFromAccountDiv = function(){
			$scope.divToShow = "showWithdraw";
		};
		$scope.showCreateNewDepositDiv = function(){
			$scope.divToShow = "showCreateNewDeposit";
		};
		$scope.showPreOpenDepositDiv = function(){
			$scope.divToShow = "showPreOpenDeposit";
		};
		$scope.showViewActivitiesDiv = function(){
			showClientActivities();
			$scope.divToShow = "showViewActivities";
		};
		/*
		 * update client
		 */
		$scope.updateClient = function(client){
			$scope.clientUpdateError = null;
			$scope.clientUpdateSuccess = null;
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/updateClientDestails";
			var onUserUpdateComplete = function(response) {
				$scope.clientUpdateSuccess = "You're account information updated";
			};
			var omUserUpdateError = function(reason) {
				$scope.clientUpdateError = "Updating you're account information failed";
			};
			mbank.sendDataToMbank(url, client).then(onUserUpdateComplete, omUserUpdateError);
		};
		/*
		 * view account
		 */
		var viewAccountDetails = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/viewAccountDetails";
			var onGetAccountDetailsComplete = function(response){
				recievedAccount = response.data;
				if (recievedAccount.creditLimit == 1.7976931348623157E308) {
					recievedAccount.creditLimit = "Unlimited";
				}
				var dataToReturn = response.data;
				dataToReturn.balance = parseInt(dataToReturn.balance);
				dataToReturn.balance.toFixed(2);
				$scope.account = dataToReturn;
			};
			var onGetAccountDetailsError = function(reason){
				redirectToHomePage();
			};
			mbank.reachMBank(url).then(onGetAccountDetailsComplete, onGetAccountDetailsError);
		};
		/*
		 * view deposits.
		 */
		var viewDepositsDetails = function() {
			$scope.deposit = null;
			$scope.deposits = null;
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/viewClientDeposits";
			var count;
			var onGetDepositsComplete = function(response){
//				var deposits = response.data.wrappedDeposits;
				count = Object.keys(response.data).length;
				var one = response.data;
				if (count > 1) {
					$scope.deposit = response.data;
				}
				else {
					$scope.deposits = response.data.wrappedDeposits;
				}
				
			}
			var onGetDepoitsError = function(reason) {
				redirectToHomePage();
			};
			mbank.reachMBank(url).then(onGetDepositsComplete, onGetDepoitsError);
		};
		/*
		 * deposit to account
		 */
		$scope.depositToAccount = function(amount) {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/depositToAccount";
			$scope.depositToAccountSuccess = null;
			$scope.depositToAccountError = null;
			var onUserDepositComplete = function(response){
				$scope.depositToAccountSuccess = "An amount of "+ amount + " USD successfully deposited to your account";
				showSummary();
			};
			var onUserDepositError = function(reason){
				$scope.depositToAccountError = "Deposit failed. Please try again";
			};
			mbank.sendDataToMbank(url, amount).then(onUserDepositComplete, onUserDepositError);
		};
		/*
		 * withdraw from account
		 */
		$scope.withdraeFromAccount = function(amount) {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/withdrawFromAccount";
			$scope.withdrawFromAccountSuccess = null;
			$scope.withdrawFromAccountError = null;
			var onUserWithdrawComplete = function(response) {
				$scope.withdrawFromAccountSuccess = "An amount of "+ amount + " USD successfully been withdrawn from your account";
				showSummary();
			};
			var onUserWithdrawError = function(reason) {
				$scope.withdrawFromAccountError = "Withdrawal failed. Please try again";
			};
			mbank.sendDataToMbank(url, amount).then(onUserWithdrawComplete, onUserWithdrawError);
		};
		/*
		 * open new deposit
		 */
		$scope.openNewDeposit = function(newDeposit) {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/createNewDeposit";
			$scope.createNewDepositSuccess = null;
			$scope.createNewDepositError = null;
			var onUserCreateNewDepositComplete = function(response) {
				$scope.createNewDepositSuccess = "new deposit successfully created";
				showSummary();
			};
			var onUserCreateNewDepositError = function(reason) {
				$scope.createNewDepositError = "something went wrong, new deposit was not created. please try again.";
			};
			mbank.sendDataToMbank(url, newDeposit).then(onUserCreateNewDepositComplete, onUserCreateNewDepositError);
		};
		/*
		 * per open deposit
		 */
		$scope.preOpenDeposit = function(depositId){
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/preOpendeposit";
			$scope.preOpenDepositSuccess = null;
			$scope.preOpenDepositError = null;
			var onUserPreOpenDepositComplete = function(response) {
				$scope.preOpenDepositSuccess = "deposit "+ depositId + " successfully pre opened";
				showSummary();
			};
			var onUserPreOpenDepositError = function(reason) {
				$scope.preOpenDepositError = "deposit "+ depositId + " was not successfully pre opened, please try again";
			};
			mbank.sendDataToMbank(url, depositId).then(onUserPreOpenDepositComplete, onUserPreOpenDepositError);
		};
		/*
		 * view activities
		 */
		var showClientActivities = function() {
			var url = "http://localhost:8080/MBankApp/MBank/MBankRoot/viewClientActivities";
			var onViewActivitiesComplete = function(response){
				var activities = response.data.actvities;
				$scope.activities = activities;
			}
			var onViewActivitiesError = function(reason) {
				redirectToHomePage();
			};
			mbank.reachMBank(url).then(onViewActivitiesComplete, onViewActivitiesError);
		};
		
	};

	app.controller("ClientController", ClientController);

}());