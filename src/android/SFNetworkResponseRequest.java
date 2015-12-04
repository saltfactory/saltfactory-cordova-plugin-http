package net.saltfactory.cordova.plugin.http;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by saltfactory on 12/1/15.
 */
class SFNetworkResponseRequest extends Request<NetworkResponse> {
    private Response.Listener<NetworkResponse> listener;
    private Map<String, Object> requestObject;


    public SFNetworkResponseRequest(int method, String urlString, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, urlString, errorListener);
        this.listener = listener;
    }

    public SFNetworkResponseRequest(int method, String urlString, Map<String, Object> requestObject, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, urlString, errorListener);
        this.listener = listener;
        this.requestObject = requestObject;
    }


    public SFNetworkResponseRequest(String urlString, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        this(Method.GET, urlString, listener, errorListener);
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected Map getParams(){
        return requestObject;
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    public static String parseToString(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return parsed;
    }

}