//
//  SFCordovaHttp.h
//  SFCordovaHttpProject
//
//  Created by SungKwang Song <saltfactory@gmail.com> on 11/3/15.
//
//

//#import <Cordova/Cordova.h>
#import <Cordova/CDV.h>
@class AFHTTPRequestOperationManager;

@interface SFCordovaPluginHttp : CDVPlugin
- (void)request:(CDVInvokedUrlCommand *)command;
- (void)requestJson:(CDVInvokedUrlCommand *)command;
@property (nonatomic, strong) NSString* urlString;
@property (nonatomic, strong) NSString* method;
@property (nonatomic) BOOL jsonable;
@property (nonatomic) BOOL securable;
@property (nonatomic, strong) NSString *responseType;
@property (nonatomic, strong) NSDictionary *data;
@property (nonatomic, strong) NSDictionary *params;
@property (nonatomic, strong) NSDictionary *headers;
@property (nonatomic, strong) CDVInvokedUrlCommand* command;
@end
