package com.ipcamera.demo.net;



import com.ipcamera.demo.bean.ErrorBean;
import com.ipcamera.demo.bean.JsonBean;
import com.ipcamera.demo.utils.GsonUtils;
import com.ipcamera.demo.utils.Log;
import com.ipcamera.demo.utils.StringUtils;

import okhttp3.Request;


/*
 * 编写：wenlong on 2017/9/28 17:37
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class VcmApi {
    private static VcmApi api;

    public static VcmApi get() {
        if (api == null) {
            synchronized (VcmApi.class) {
                if (api == null) {
                    api = new VcmApi();
                }
            }
        }
        return api;
    }

//    public void load(final BaseRequest requsbean, final ApiCallBack callBack){
//        HttpHelper.getInstance().post(requsbean.getUrl(), GsonUtils.getJson(requsbean), new BaseCallback() {
//            @Override
//            public void onFailure(Request request, Exception e) {
//                onFailureLog(requsbean.getUrl(),GsonUtils.getJson(requsbean),e.getMessage());
//                callBack.onError(new ErrorBean(0, e.getMessage()));
//            }
//
//            @Override
//            public void onResponse(int code, String json) {
//                onResponseLog(code,requsbean.getUrl(), GsonUtils.getJson(requsbean),json);
//                if (code == 200) {
//                 callBack.onFinish(new JsonBean(code, json));
//                } else {
//                    callBack.onError(new ErrorBean(code,json+"\n"+getErrorMsg(code)));
//                }
//            }
//        });
//    }
    public void load(final String url,final String params,final ApiCallBack callBack){
        HttpHelper.signature= StringUtils.getSignature(params);

        HttpHelper.getInstance().post(url,params, new BaseCallback() {
            @Override
            public void onFailure(Request request, Exception e) {
                onFailureLog(url,params,e.getMessage());
                callBack.onError(new ErrorBean(0, e.getMessage()));
            }

            @Override
            public void onResponse(int code, String json) {
                onResponseLog(code,url,params,json);
                if (code == 200) {
                    callBack.onFinish(new JsonBean(code, json));
                } else {
                    callBack.onError(new ErrorBean(code,json+"\n"+getErrorMsg(code)));
                }
            }
        });
    }

    public void load(final String url,final Object object,final ApiCallBack callBack){
        HttpHelper.signature= StringUtils.getSignature(GsonUtils.getJson(object));
        Log.e("api",HttpHelper.signature);
        Log.e("api","GsonUtils.getJson(object)"+GsonUtils.getJson(object));
        HttpHelper.getInstance().post(url, GsonUtils.getJson(object), new BaseCallback() {
            @Override
            public void onFailure(Request request, Exception e) {
                onFailureLog(url,GsonUtils.getJson(object),e.getMessage());
                callBack.onError(new ErrorBean(0, e.getMessage()));
            }

            @Override
            public void onResponse(int code, String json) {
                onResponseLog(code,url,GsonUtils.getJson(object),json);
                if (code == 200) {
                    callBack.onFinish(new JsonBean(code, json));
                } else {
                    callBack.onError(new ErrorBean(code,json+"\n"+getErrorMsg(code)));
                }
            }
        });
    }
    public void cancle(){

    }
    protected static String getErrorMsg(int error) {
        String msg = "";
        switch (error) {
            case 400:
                msg = "上传参数格式错误，参数缺失或格式不正确";
                break;

            case 401:
                msg = "密码、授权码或验证码无效";
                break;

            case 403:
                msg = "资源已存在，服务器不响应请求(新注册场景时，用户已存在；添加设备场景时，设备已存在)";
                break;

            case 404:
                msg = "请求资源不存在（用户不存在、设备不存在、邮箱没绑定、电话没绑定）";
                break;

            case 500:
                msg = "服务器内部错误，请联系技术客服 企业QQ：2853239883";
                break;

            case 504:
                msg = "Gateway Timeout，说明代理网关已经收到了请求，但转发请求失败，客户端收到状态码后，建议重发3次以提高用户体验";
                break;

            case 550:
                msg = "没有权限对资源进行操作";
                break;

            case 601:
                msg = "无法获取用户信息，请重新登录";
                break;
            default:
                msg = "服务器内部错误，请联系技术客服 企业QQ：2853239883";
        }
        return msg;
    }
    protected void onResponseLog(int code,String url,String params,String json){
        Log.print("------------------------onResponse------------------------");
        Log.print("请求连接：" + url);
        Log.print("请求参数：" +params);
        Log.print("请求结果code：" + code + "--json=" + json);
        if (code!=200){
            Log.print("请求失败原因："+getErrorMsg(code));
        }
        Log.print("----------------------------------------------------------");
    }
    protected void onFailureLog(String url,String param,String msg){
        Log.print("------------------------onFailure-------------------------");
        Log.print("请求连接：" + url);
        Log.print("请求参数：" + param);
        Log.print("请求失败：" + msg);
        Log.print("请求失败原因：请检测网络或查看代码是否有调用网络权限");
        Log.print("----------------------------------------------------------");
    }
}
