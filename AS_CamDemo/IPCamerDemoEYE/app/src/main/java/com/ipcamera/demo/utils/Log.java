package com.ipcamera.demo.utils;

/*
 * 编写：vst  on 2017/9/28 17:04
 * //
 * //
 */
public class Log {
    public static boolean isDebug=true;
    public static void i(String tag,String info){
        if (isDebug)
        System.out.println(tag+"---"+info);
    }
    public static void e(String tag,String info){
        if (isDebug)
            System.out.println(tag+"---"+info);
    }
    public static void print(String info){
        if (isDebug)
            Log.e("api","LOG PRINTLN: "+info);
    }
}
