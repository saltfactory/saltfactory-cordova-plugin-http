package net.saltfactory.cordova.plugin.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by saltfactory on 12/1/15.
 */
public class SFUrlBuilder {
    private String baseUrl;
    private StringBuilder urlBuilder;

    public SFUrlBuilder(String baseUrl){
        this.baseUrl = baseUrl;
        this.urlBuilder = new StringBuilder(baseUrl);
    }


    private String urlencode(String s){
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }



    public SFUrlBuilder params(String key, Object value){
        urlBuilder.append(String.format("%s%s=%s", this.baseUrl.contains("?") ? "&" : "?"  , urlencode(key), urlencode(String.valueOf(value))));
        return this;
    }


    public String build(){
        return this.urlBuilder.toString();
    }


}
