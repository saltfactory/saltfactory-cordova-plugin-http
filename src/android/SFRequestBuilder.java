package net.saltfactory.cordova.plugin.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by saltfactory on 12/1/15.
 */

public class SFRequestBuilder {
    private final String TAG = "SFRequestBuilder";

    private CallbackContext callbackContext;
    private PluginResult pluginResult;
    private int method;
    //    private String url;
    private SFUrlBuilder urlBuilder;

    private boolean jsonable;
    private Response.Listener<NetworkResponse> listener;
    private Response.ErrorListener errorListener;
    private Request request;
    private Map<String, String> headers;
    private Map<String, String> data;
    private Map<String, String> params;
    private Map<String, Object> resultInfo;

    public SFRequestBuilder() {
        this.method = Request.Method.GET;
        this.jsonable = false;
        this.headers = new HashMap<String, String>();
        this.data = new HashMap<String, String>();
        this.params = new HashMap<String, String>();
    }

    public SFRequestBuilder callbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        return this;
    }

    public SFRequestBuilder method(int method) {
        this.method = method;
        return this;
    }

    public SFRequestBuilder method(String method){
        String _method = method.toUpperCase();
        if (_method.equals("GET")){
            this.method = Request.Method.GET;
        } else if(_method.equals("POST")){
            this.method = Request.Method.POST;
        } else if(_method.equals("PUT")) {
            this.method = Request.Method.PUT;
        } else if(_method.equals("PATCH")){
            this.method = Request.Method.PATCH;
        } else if(_method.equals("DELETE")){
            this.method = Request.Method.DELETE;
        } else if(_method.equals("OPTIONS")){
            this.method = Request.Method.OPTIONS;
        } else if(_method.equals("HEAD")) {
            this.method = Request.Method.HEAD;
        } else if(_method.equals("TRACE")) {
            this.method = Request.Method.TRACE;
        } else if(_method.equals("DEPRECATED_GET_OR_POST")) {
            this.method = Request.Method.DEPRECATED_GET_OR_POST;
        } else {
            this.method = Request.Method.GET;
        }
        return this;
    }

    public SFRequestBuilder url(String url) {
//        this.url = url;
        this.urlBuilder = new SFUrlBuilder(url);
        return this;
    }

    public SFRequestBuilder listener(Response.Listener<NetworkResponse> listener) {
        this.listener = listener;
        return this;
    }

    public SFRequestBuilder errorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public SFRequestBuilder jsonable(boolean jsonable) {
        this.jsonable = jsonable;
        return this;
    }

    public SFRequestBuilder headers(String key, Object value) {
        this.headers.put(key, String.valueOf(value));
        return this;
    }

    public SFRequestBuilder headers(Map<String, Object> headers) {
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                this.headers.put(key, String.valueOf(headers.get(key)));
            }
        }

        return this;
    }

    public SFRequestBuilder headers(JSONObject headersJson) {
        try {
            Iterator<String> keys = headersJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = headersJson.get(key);
                this.headers.put(key, String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public SFRequestBuilder data(String key, Object value) {
        this.data.put(key, String.valueOf(value));
        return this;
    }

    public SFRequestBuilder data(Map<String, Object> data) {
        if (data != null) {
            Set<String> keys = data.keySet();
            for (String key : keys) {
                this.data.put(key, String.valueOf(data.get(key)));
            }
        }
        return this;
    }

    public SFRequestBuilder data(JSONObject dataJson) {
        try {
            Iterator<String> keys = dataJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = dataJson.get(key);
                this.data.put(key, String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public SFRequestBuilder params(String key, Object value) {
        this.params.put(key, String.valueOf(value));
        return this;
    }

    public SFRequestBuilder params(Map<String, Object> data) {
        if (data != null) {
            Set<String> keys = data.keySet();
            for (String key : keys) {
                this.params.put(key, String.valueOf(data.get(key)));
            }
        }
        return this;
    }

    public SFRequestBuilder params(JSONObject paramsJson) {
        try {
            Iterator<String> keys = paramsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = paramsJson.get(key);
                this.params.put(key, String.valueOf(value));
                this.urlBuilder.params(key, String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }


    public Request build() {
        resultInfo = new HashMap<String, Object>();

        if (listener == null) {
            this.listener = new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                    Map<String, Object> resultMap = new HashMap<String, Object>();
                    Map<String, String> headers = response.headers;

                    resultMap.put("headers", headers);
//                    resultMap.put("data", SFNetworkResponseRequest.parseToString(response));

                    String responseBody = SFNetworkResponseRequest.parseToString(response);

                    if (jsonable) {
                        try {
                            JSONObject resultJson = new JSONObject(resultMap);

                            Object jsonObject = new JSONTokener(responseBody).nextValue();
                            resultJson.put("data", jsonObject);

                            Log.d(TAG, resultJson.toString());

                            pluginResult = new PluginResult(PluginResult.Status.OK, resultJson);

                        } catch (JSONException e) {
                            resultMap.put("error", e.getLocalizedMessage());
                            JSONObject resultJson = new JSONObject(resultMap);

                            e.printStackTrace();
                            Log.e(TAG, e.getLocalizedMessage());
                            Log.d(TAG, resultJson.toString());

                            pluginResult = new PluginResult(PluginResult.Status.ERROR, resultJson);
                        }
                    } else {
                        resultMap.put("data", responseBody);
                        JSONObject resultJson = new JSONObject(resultMap);
                        pluginResult = new PluginResult(PluginResult.Status.OK, resultJson);
                    }

                    callbackContext.sendPluginResult(pluginResult);
                }
            };
        }

        if (errorListener == null) {
            this.errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.getLocalizedMessage());
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, error.getLocalizedMessage());
                    callbackContext.sendPluginResult(pluginResult);
                }
            };
        }

        // build request url with params
        String urlString = this.urlBuilder.build();


        this.request = new SFNetworkResponseRequest(this.method, urlString, this.listener, this.errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if(jsonable){
                    headers.put("Content-Type", "application/json;charset=utf-8");
                }

                return headers;
            }

            @Override
            public Map<String, String> getParams() {
                return data;
            }

            @Override
            public byte[] getBody() {
                //                        String body = new JSONObject(data).toString();
                try {
                    return new JSONObject(data).toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        return this.request;


    }


}
