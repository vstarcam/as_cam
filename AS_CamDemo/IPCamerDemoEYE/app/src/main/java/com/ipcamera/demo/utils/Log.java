package com.ipcamera.demo.utils;

/*
 * 编写：wenlong on 2017/9/28 17:04
 * 企业QQ： 2853239883
 * 钉钉：13430330686
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
            System.out.println("LOG PRINTLN: "+info);
    }
}
