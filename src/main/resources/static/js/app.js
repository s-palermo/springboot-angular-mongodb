var app = angular.module('app', ['ngRoute','ngResource','ngTable']);
app.config(function($routeProvider){
    $routeProvider
        .when('/invoice-list',{
            templateUrl: '/views/invoice-list.html',
            controller: 'listController'
        })
        .when('/invoice-create',{
            templateUrl: '/views/invoice-create.html',
            controller: 'createController'
        })
        .when('/invoice-update',{
            templateUrl: '/views/invoice-update.html',
            controller: 'updateController'
        })
        .otherwise(
            { redirectTo: '/invoice-list'}
        );
});

var _config = (function() {//load environment variables
    var json = null;
    $.ajax({
        'async': false,
        'global': false,
        'url': "/config.json",
        'dataType': "json",
        'success': function (data) {
            json = data;
        }
    });
    return json;
})();

$(function () {

	var links = $('.sidebar-links > a');

	links.on('click', function () {

		links.removeClass('selected');
		$(this).addClass('selected');

	})
});