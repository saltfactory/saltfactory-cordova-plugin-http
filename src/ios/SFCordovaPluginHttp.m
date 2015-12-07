//
//  SFCordovaHttp.m
//  SFCordovaHttpProject
//
//  Created by SungKwang Song <saltfactory@gmail.com> on 11/3/15.
//
//

#import "SFCordovaPluginHttp.h"

#import "AFHTTPRequestOperationManager.h"

@implementation SFCordovaPluginHttp

- (void)initCommand
{
    self.urlString = nil;
    self.method = nil;
    self.headers = nil;
    self.data = nil;
    self.params = nil;
    self.jsonable = NO;
    self.securable = NO;
    self.command = nil;
    self.responseType = nil;
}

// result to JavaScript interface, private functions
- (void)sendPluginResult:(NSDictionary *)resultInfo withStatus:(CDVCommandStatus)status
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:status messageAsDictionary:resultInfo];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];

    [self initCommand];
}

- (void)sendPluginResultOKWithDictionary:(NSDictionary *)resultInfo
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultInfo];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
    
    [self initCommand];
}


- (void)sendPluginResultError:(NSError *)error
{
    NSLog(@"%@", error.localizedDescription);
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
    
    [self initCommand];
}


- (void)parseRequestInfo:(NSDictionary*)requestInfo
{
    self.urlString = [requestInfo valueForKey:@"url"];
    self.method = [requestInfo valueForKey:@"method"] == nil ? @"GET" : [[requestInfo valueForKey:@"method"] uppercaseString];
    self.headers = [NSMutableDictionary dictionaryWithDictionary:[requestInfo valueForKey:@"headers"]];
    self.data = [NSMutableDictionary dictionaryWithDictionary:[requestInfo valueForKey:@"data"]];
    self.params = [NSMutableDictionary dictionaryWithDictionary:[requestInfo valueForKey:@"params"]];
    self.jsonable = [requestInfo valueForKey:@"jsonable"];
    self.securable = [requestInfo valueForKey:@"securable"];
    self.responseType = [[requestInfo valueForKey:@"responseType"] uppercaseString];
    
    
    //create URL query string by self.params
    NSArray *keys =  [self.params allKeys];
    for (NSString *key in keys) {
        NSString *keyValue = [NSString stringWithFormat:@"%@=%@", key, [self.params valueForKey:key]];
        self.urlString = [self.urlString stringByAppendingString:[NSString stringWithFormat:@"%@%@", [self.urlString containsString:@"?"] ? @"&" : @"?",keyValue ]];
    }

}

- (void)parseCommand:(CDVInvokedUrlCommand *)command
{
    self.command = command;
    NSDictionary *requestInfo = [command.arguments objectAtIndex:0];
    [self parseRequestInfo:requestInfo];
}

-(AFHTTPRequestOperationManager *)createHttpRequestOperationManager
{
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    //    manager.responseSerializer = [SFResponseSerializer serializer];

    //TODO: SSL verification
    manager.securityPolicy.allowInvalidCertificates = YES;
    manager.securityPolicy.validatesDomainName = NO;
//
////    AFSecurityPolicy *policy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
//        AFSecurityPolicy *policy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
//    [policy setAllowInvalidCertificates:YES];
//    [policy setValidatesDomainName:NO];
//    [manager setSecurityPolicy:policy];
////    policy.allowInvalidCertificates = YES;
////    policy.validatesDomainName = NO;
////    manager.securityPolicy = policy;
    
    if (self.jsonable) {
        manager.requestSerializer = [AFJSONRequestSerializer serializer];
        manager.responseSerializer = [AFJSONResponseSerializer serializer];
    } else {
        manager.requestSerializer = [AFHTTPRequestSerializer serializer];
        manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    }
    
    for (NSString *key in [self.headers allKeys]) {
        [manager.requestSerializer setValue:[self.headers valueForKey:key] forHTTPHeaderField:key];
    }
    
    
    if ([self.method isEqualToString:@"POST"]) {
        manager.completionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    }
    
    return manager;
}

-(void)addHttpRequestOperationToManager:(AFHTTPRequestOperationManager *)manager
{
    NSError *serializationError = nil;
    NSMutableURLRequest *request = [manager.requestSerializer requestWithMethod:self.method URLString:self.urlString parameters:self.data error:&serializationError];
    
    if (serializationError) {
        [self sendPluginResultError:serializationError];
    }
    
    AFHTTPRequestOperation *operation = [manager HTTPRequestOperationWithRequest:request
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             
             NSDictionary *resultInfo = [NSMutableDictionary dictionary];
             
             if(self.jsonable) {
                 [resultInfo setValue:responseObject forKey:@"data"];
             } else {
                 NSString *responseString = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
                 [resultInfo setValue:responseString forKey:@"data"];
             }
             
             [resultInfo setValue:operation.response.allHeaderFields forKey:@"headers"];
             NSLog(@"%@", resultInfo);
             [self sendPluginResult:resultInfo withStatus:CDVCommandStatus_OK];
             
         }
         failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             NSLog(@"%@", error.localizedDescription);
             
             NSDictionary *resultInfo = [NSMutableDictionary dictionary];
             [resultInfo setValue:operation.response.allHeaderFields forKey:@"headers"];
             [resultInfo setValue:error.localizedDescription forKey:@"error"];
             
             [self sendPluginResult:resultInfo withStatus:CDVCommandStatus_ERROR];
             
         }];
    
    [[manager operationQueue] addOperation:operation];
    
    
}


// from JavaScript interface

- (void)request:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        [self parseCommand:command];
        
        AFHTTPRequestOperationManager *manager = [self createHttpRequestOperationManager];
        [self addHttpRequestOperationToManager:manager];
    }];
    
}

- (void)requestJson:(CDVInvokedUrlCommand *)command
{
    [self parseCommand:command];
    
    self.jsonable = true;
//    [self.headers setValue:@"application/json" forKey:@"Content-Type"];
    
    AFHTTPRequestOperationManager *manager = [self createHttpRequestOperationManager];
    [self addHttpRequestOperationToManager:manager];

}
@end
    