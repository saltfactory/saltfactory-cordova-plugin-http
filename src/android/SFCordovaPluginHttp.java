package net.saltfactory.cordova.plugin.http;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class SFCordovaPluginHttp extends CordovaPlugin {
    final String TAG = "SFCordovaPluginHttp";
    private final String ACTION_REQUEST = "REQUEST";
    private final String ACTION_REQUEST_JSON = "REQUESTJSON";

    private void runRquest(final Request<NetworkResponse> request){
        cordova.getThreadPool().execute(new Runnable(){

            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(cordova.getActivity(), new SFHttpStack());
                requestQueue.add(request);
            }
        });
    }

    @Override
    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        String argsString = args.getString(0);

        JSONObject requestInfo = new JSONObject(argsString);

        String urlString = requestInfo.getString("url");
        String method = requestInfo.getString("method").toUpperCase();

        JSONObject headersJson = null;
        JSONObject paramsJson = null;
        JSONObject dataJson = null;

        try {
            headersJson  = requestInfo.getJSONObject("headers");
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            paramsJson = requestInfo.getJSONObject("params");
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            dataJson = requestInfo.getJSONObject("data");
        } catch (Exception e){
            e.printStackTrace();
        }


        boolean jsonable = false;

        try {
            jsonable = requestInfo.getBoolean("jsonable");
        } catch (Exception e){
            e.printStackTrace();
        }

        Request<NetworkResponse> request = new SFRequestBuilder()
                .method(method)
                .url(urlString)
                .headers(headersJson)
                .params(paramsJson)
                .data(dataJson)
                .jsonable(jsonable)
                .callbackContext(callbackContext)
                .build();

        runRquest(request);
        return true;

//        if (action.toUpperCase().equals(ACTION_REQUEST)) {
//
//            try {
//                jsonable = requestInfo.getBoolean("jsonable");
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
//
////            if (method.equals("GET")) {
//                Request<NetworkResponse> request = new SFRequestBuilder()
//                        .method(method)
//                        .url(urlString)
//                        .headers(headersJson)
//                        .params(paramsJson)
//                        .data(dataJson)
//                        .jsonable(jsonable)
//                        .callbackContext(callbackContext)
//                        .build();
//
//                runRquest(request);
//                return true;
////            } else {
////
////            }
////            return true;
//        } else if(action.toUpperCase().equals(ACTION_REQUEST_JSON)){
//            jsonable = true;
//
//
////            headersJson.put("Content-Type", "application/x-www-form-urlencoded");
//            headersJson.put("Content-Type", "application/json;charset=utf-8");
//
//            SFRequestBuilder requestBuilder = new SFRequestBuilder()
//                    .url(urlString)
//                    .headers(headersJson)
//                    .jsonable(jsonable)
//                    .params(paramsJson)
//                    .callbackContext(callbackContext);
//
//            if(method.equals("GET")){
////                headersJson.put("Content-Type", "application/json");
//                requestBuilder.method(Request.Method.GET);
//            } else if(method.equals("POST")) {
//                requestBuilder.method(Request.Method.POST);
//                requestBuilder.data(dataJson);
//            } else if(method.equals("PUT")) {
//                requestBuilder.method(Request.Method.PUT);
//                requestBuilder.data(dataJson);
//            } else if(method.equals("PATCH")) {
////                headersJson.put("Content-Type", "application/json;charset=utf-8");
////                requestBuilder.headers("Content-Type", "application/json;charset=utf-8");
//                requestBuilder.method(Request.Method.PATCH);
//                requestBuilder.data(dataJson);
//            } else if(method.equals("DELETE")) {
//                requestBuilder.method(Request.Method.DELETE);
//                requestBuilder.data(dataJson);
//
//            } else {
////                requestBuilder.method(Request.Method.GET);
//                //TODO: return error;
//            }
//
////            Request<NetworkResponse> request = new SFRequestBuilder()
////                    .method(Request.Method.GET)
////                    .url(urlString)
////                    .headers(headersJson)
////                    .jsonable(jsonable)
////                    .params(paramsJson)
////                    .callbackContext(callbackContext)
////                    .build();
//
//            runRquest(requestBuilder.build());
//
//            return true;
//        }
//        return true;
    }
}