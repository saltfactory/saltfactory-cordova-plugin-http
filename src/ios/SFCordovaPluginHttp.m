//
//  SFCordovaHttp.m
//  SFCordovaHttpProject
//
//  Created by SungKwang Song on 11/3/15.
//
//

#import "SFCordovaPluginHttp.h"

#import "AFHTTPRequestOperationManager.h"

@interface SFResponseSerializer: AFHTTPResponseSerializer
@end

@implementation SFResponseSerializer
-(id)responseObjectForResponse:(NSURLResponse *)response data:(NSData *)data error:(NSError *__autoreleasing *)error
{
    id result;
    
    if ([response.MIMEType isEqualToString:@"application/json"]) {
        NSError *parsingError;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data
                                                                     options:NSJSONReadingAllowFragments
                                                                       error:&parsingError];
        if (parsingError) {
            *error = parsingError;
        } else {
            result = json;
        }
    } else {
        result = [NSString stringWithUTF8String:[data bytes]];
    }
    
    return result;
}

@end



@implementation SFCordovaPluginHttp


// result to JavaScript interface, private functions
- (void)sendPluginResultOKWithDictionary:(NSDictionary *)resultInfo command:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultInfo];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


- (void)sendPluginResultError:(NSError *)error command:(CDVInvokedUrlCommand *)command
{
    NSLog(@"%@", error.localizedDescription);
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
    
    NSString *urlString = [requestInfo valueForKey:@"url"];
    NSString *method = [[requestInfo valueForKey:@"method"] uppercaseString];
    NSString *responseType = [[requestInfo valueForKey:@"responseType"] lowercaseString];
    responseType = responseType == nil ? @"json" : responseType;
    
    NSDictionary *data = [requestInfo valueForKey:@"data"];
    NSDictionary *headers = [requestInfo valueForKey:@"headers"];
    
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [SFResponseSerializer serializer];
    manager.securityPolicy.allowInvalidCertificates = YES;
    manager.securityPolicy.validatesDomainName = NO;
    for (NSString *key in [headers allKeys]) {
        [manager.requestSerializer setValue:[headers valueForKey:key] forHTTPHeaderField:key];
    }
    
    if ([method isEqualToString:@"POST"]) {
        [manager POST:urlString parameters:data
              success:^(AFHTTPRequestOperation *operation, id responseObject) {
                  [self sendPluginResultOKWithDictionary:responseObject command:command];
              }
              failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                  [self sendPluginResultError:error command:command];
              }];
    } else {
        NSDictionary *params= [requestInfo valueForKey:@"params"];

        [manager GET:urlString parameters:params
             success:^(AFHTTPRequestOperation *operation, id responseObject) {
                 [self sendPluginResultOKWithDictionary:responseObject command:command];
             }
             failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                 [self sendPluginResultError:error command:command];
             }];
    }
}
@end
