package com.ipcamera.demo.utils;

import com.google.gson.Gson;

/*
 * 编写：wenlong on 2017/9/28 18:41
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class GsonUtils {
    public static String getJson(Object object){
        return new Gson().toJson(object);
    }
}
