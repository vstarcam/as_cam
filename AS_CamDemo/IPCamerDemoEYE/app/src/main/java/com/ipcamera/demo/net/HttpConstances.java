package com.ipcamera.demo.net;

/*
 * 编写：vst  on 2017/10/30 17:39
 * //
 * //
 */
public class HttpConstances {
    //public static final String BASE_SERVICE_URL = "https://api.eye4.cn";  //正式 http://push2.eye4.cn:3000/binding
    public static final String BASE_SERVICE_URL="http://push2.eye4.cn:3000";
    //基于设备的app推送
    //绑定接口
    public static String BAND_APP_PUSH_BY_DEVICES=BASE_SERVICE_URL+"/binding";
    //解绑接口
    public static String UNBAND_APP_PUSH_BY_DEVICES=BASE_SERVICE_URL+"/unbind";
    //设置语言
    public static String SETLANGUAGE=BASE_SERVICE_URL+"/setLanguage";

    //获取记录
    public static String GETINFO = BASE_SERVICE_URL+"/getRecord";
}
