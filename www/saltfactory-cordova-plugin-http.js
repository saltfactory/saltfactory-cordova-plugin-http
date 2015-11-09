var exec = require('cordova/exec');

// exports.echo = function(arg0, success, error) {
//     exec(success, error, "saltfactory-cordova-plugin-http", "echo", [arg0]);
// };

var SFCordovaHttp = function(){};

SFCordovaHttp.prototype.echo = function(message, callbackSuccess, callbackError ) {
	exec(callbackSuccess, callbackError, "saltfactory-cordova-plugin-http", "echo", [message]);
}

SFCordovaHttp.prototype.request = function(requestInfo, callbackSuccess, callbackError){
	exec(callbackSuccess, callbackError, "saltfactory-cordova-plugin-http", "request", [requestInfo]);
}

module.exports = new SFCordovaHttp();