app.controller("listController", ["$scope", "$http", "NgTableParams", function($scope,$http, NgTableParams) {	
	var menuButton = document.getElementById("side-menu-list");
	$(menuButton).addClass('link-blue selected');
	
	$scope.invoices = [];
	$scope.getInvoices = function() {
		console.debug("Loading Invoices");
		$http({
			  method: 'GET',
			  url: _config.baseUrl + '/api/invoice/getall'
			}).then(function successCallback(response) {
			  $scope.invoices = response.data.invoices;	
			  $scope.invoicesTable = new NgTableParams({
				    page: 1,
				    count: 10
				  }, {
				    counts: [],
				    total: $scope.invoices.length,
				    dataset: $scope.invoices
				  });
			    console.debug(response);
			  }, function errorCallback(response) {
			    console.error(response);
			  });
	}
	$scope.getInvoices();  
}]);

app.controller("updateController", ["$scope","$http","$timeout", function($scope,$http,$timeout) {
	var menuButton = document.getElementById("side-menu-update");
	$(menuButton).addClass('link-yellow selected');
	
	$scope.poRegex = new RegExp('Z[A-F0-9]{10}\\b');
	$scope.invoices = [];
	$scope.getInvoices = function() {
		console.debug("Loading Invoices");  
		$http({
			  method: 'GET',
			  url:  _config.baseUrl + '/api/invoice/getall'
			}).then(function successCallback(response) {
				var rawInvoices = response.data.invoices;				
				$scope.invoices = cleanInvoices(rawInvoices); 				  
			    console.debug(response);
			  }, function errorCallback(response) {
			    console.error(response);
			  });
	}
	$scope.getInvoices();
		
	$scope.deleteInvoice = function() {
		$scope.errors = null;	
		$http({
			  method: 'DELETE',
			  url:  _config.baseUrl + '/api/invoice/delete?invoiceId=' + $scope.invoice.id
			}).then(function successCallback(response) {	
				$scope.invoice = null;
				$scope.messages = "Success!";
				$scope.getInvoices();
				$scope.updateForm.$setPristine();
			    console.debug(response);
			  }, function errorCallback(response) {
				$scope.errors = response.data.error; 			
			    console.debug(response);
			  }).finally(function() {
				  $timeout(function() {
				        $scope.messages = null;
				  }, 3000);				  
		});		
	}
	
	$scope.updateInvoice = function() {		
		$scope.errors = null;
		if ($scope.updateForm.$valid) {
			console.log("Creating Invoice");
			$scope.invoice.due_date = formatDate($scope.invoice.due_date);
			$scope.invoice.invoice_date = formatDate($scope.invoice.invoice_date);
			$http({
				  method: 'PUT',
				  url:  _config.baseUrl + '/api/invoice/update',
				  data: $scope.invoice				  
				}).then(function successCallback(response) {	
					$scope.invoice = null;
					$scope.messages = "Success!";
					$scope.getInvoices();
					$scope.updateForm.$setPristine();
				    console.debug(response);
				  }, function errorCallback(response) {
					$scope.errors = response.data.error;  					
				    console.error(response);
				  }).finally(function() {
					  $timeout(function() {
					        $scope.messages = null;
					  }, 3000);				  
			});
		}
	};
}]);

app.directive('ngConfirmClick', [
    function(){
        return {
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click',function (event) {
                    if ( window.confirm(msg) ) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
}])


app.controller("createController", ["$scope","$http", "$timeout", function($scope,$http,$timeout) {
	var menuButton = document.getElementById("side-menu-create");
	$(menuButton).addClass('link-red selected');
	
	$scope.poRegex = new RegExp('Z[A-F0-9]{10}\\b');
		
	$scope.submitForm = function() {		
		$scope.errors = null;
		if ($scope.invoiceForm.$valid) {
			console.log("Creating Invoice");
			$scope.invoice.due_date = formatDate($scope.invoice.due_date);
			$scope.invoice.invoice_date = formatDate($scope.invoice.invoice_date);
			$http({
				  method: 'POST',
				  url:  _config.baseUrl + '/api/invoice/create',
				  data: $scope.invoice				  
				}).then(function successCallback(response) {	
					$scope.invoice = null;
					$scope.messages = "Success!";
					$scope.invoiceForm.$setPristine();
				    console.debug(response);
				  }, function errorCallback(response) {
					$scope.errors = response.data.error;  					
				    console.error(response);
				  }).finally(function() {
					  $timeout(function() {
					        $scope.messages = null;
					  }, 3000);				  
			});
		}
	};

}]);