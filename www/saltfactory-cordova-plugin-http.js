var exec = require('cordova/exec');

var SFCordovaPluginHttp = function(){};

SFCordovaPluginHttp.prototype.request = function(requestInfo, callbackSuccess, callbackError){
	exec(callbackSuccess, callbackError, "saltfactory-cordova-plugin-http", "request", [requestInfo]);
}

SFCordovaPluginHttp.prototype.requestJson = function(requestInfo, callbackSuccess, callbackError){
	exec(callbackSuccess, callbackError, "saltfactory-cordova-plugin-http", "requestJson", [requestInfo]);
}

// module.exports = new SFCordovaPluginHttp();
module.exports = SFCordovaPluginHttp;