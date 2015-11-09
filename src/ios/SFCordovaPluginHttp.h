//
//  SFCordovaHttp.h
//  SFCordovaHttpProject
//
//  Created by SungKwang Song on 11/3/15.
//
//

//#import <Cordova/Cordova.h>
#import <Cordova/CDV.h>

@interface SFCordovaPluginHttp : CDVPlugin
- (void)echo:(CDVInvokedUrlCommand *)command;
- (void)request:(CDVInvokedUrlCommand *)command;
@end
