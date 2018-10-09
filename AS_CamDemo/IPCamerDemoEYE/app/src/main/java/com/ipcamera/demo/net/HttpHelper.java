package com.ipcamera.demo.net;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
 * 编写：wenlong on 2017/9/28 16:39
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class HttpHelper {
    public static final String TAG = "OkHttpHelper";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static HttpHelper mInstance;
    private OkHttpClient mHttpClient;

    private Handler mHandler;

    static{
        mInstance = new HttpHelper();
    }

    private HttpHelper(){

        mHttpClient = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();
        mHandler = new Handler(Looper.getMainLooper());

    }

    public static HttpHelper getInstance(){
        return mInstance;
    }


    public void get(String url, BaseCallback callback){
        get(url, null, callback);
    }

    public void get(String url, Map<String, Object> param, BaseCallback callback){
        Request request = buildGetRequest(url, param);
        request(request,callback);
    }

    public void post(String url, Map<String, Object> param, BaseCallback callback){
        Request request = buildPostRequest(url, param);
        request(request, callback);

    }

    public void post(String url, String json, BaseCallback callback){
        Request request = buildPostRequest(url, json);
        request(request, callback);
    }


    private Request buildGetRequest(String url, Map<String, Object> param){
        return buildRequest(url, HttpMethodType.GET,param);
    }

    private Request buildPostRequest(String url, Map<String, Object> param){
        return buildRequest(url, HttpMethodType.POST,param);
    }

    private Request buildPostRequest(String url, String json){
        return buildRequest(url, HttpMethodType.POST,json);
    }


    private Request buildRequest(String url, HttpMethodType methodType, Map<String, Object> params){
        return buildRequest(url, HttpMethodType.POST,   new Gson().toJson(params));
    }
public static String signature="";
    private Request buildRequest(String url, HttpMethodType methodType, String json){
        Request.Builder build = new Request.Builder()
                .url(url);
        build.addHeader("signature",signature);
        build.addHeader("accessKey", "af9aec75-1e24-107a-4252-14cffaabcfb2");
        if(methodType == HttpMethodType.GET){
        }else if(methodType == HttpMethodType.POST){
            RequestBody requestBody = RequestBody.create(JSON, json);
            build.post(requestBody);
        }

        return build.build();

    }

    private RequestBody builderFormData(Map<String, String> params){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }

        return builder.build();
    }

    private void request(final Request request,final BaseCallback callback){
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(callback, request, e);
            }

            @Override
            public void onResponse(Call call, Response response)  {
                String json = null;
                try {
                    json = response.body().string();
                    callbackResponse(callback,response.code(),json);
                } catch (IOException e) {
                    callbackFailure(callback, null, e);
                }

            }
        });
    }



    private void callbackFailure(final BaseCallback callback, final Request request, final IOException e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }

    private void callbackResponse(final BaseCallback callback,final int code,final String json){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(code,json);
            }
        });
    }


    enum HttpMethodType{
        GET,
        POST,
    }
}
