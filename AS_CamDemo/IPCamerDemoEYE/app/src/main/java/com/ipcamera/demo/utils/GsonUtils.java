package com.ipcamera.demo.utils;

import com.google.gson.Gson;

/*
 * 编写：vst  on 2017/9/28 18:41
 * //
 * //
 */
public class GsonUtils {
    public static String getJson(Object object){
        return new Gson().toJson(object);
    }
}
