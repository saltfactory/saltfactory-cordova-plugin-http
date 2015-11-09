//
//  SFCordovaHttp.m
//  SFCordovaHttpProject
//
//  Created by SungKwang Song on 11/3/15.
//
//

#import "SFCordovaPluginHttp.h"

@implementation SFCordovaPluginHttp


// result to JavaScript interface, private functions
- (void)sendPluginResultOKWithDictionary:(NSDictionary *)resultInfo command:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultInfo];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


- (void)sendPluginResultError:(NSError *)error command:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


// from JavaScript interface
- (void)echo:(CDVInvokedUrlCommand *)command
{
    NSString* message = [command.arguments objectAtIndex:0];
    [[[UIAlertView alloc] initWithTitle:@"iOS 알림" message:message delegate:nil cancelButtonTitle:@"취소" otherButtonTitles:@"확인", nil] show];
}



- (void)request:(CDVInvokedUrlCommand *)command
{
    NSDictionary *requestInfo = [command.arguments objectAtIndex:0];
    NSDictionary *resultInfo = [NSDictionary dictionaryWithDictionary:requestInfo];
//    [NSDictionary dictionaryWithObjectsAndKeys:@"reuslt-sccuess", @"result", nil];
    [self sendPluginResultOKWithDictionary:resultInfo command:command];
}
@end
