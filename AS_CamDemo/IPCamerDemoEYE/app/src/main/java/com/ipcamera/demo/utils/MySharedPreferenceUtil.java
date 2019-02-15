package com.ipcamera.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * store package Name
 *
 * @author Administrator
 */
public class MySharedPreferenceUtil {

    private static SharedPreferences prefer;


    //保存设备 的信息
    public static void saveDeviceInformation(Context context, String uid, String informationType, String information) {
        prefer = context.getSharedPreferences(uid, Context.MODE_PRIVATE);
        prefer.edit().putString(informationType, information).commit();
    }

    //获取设备 的信息
    public static String getDeviceInformation(Context context, String uid, String informationType) {
        prefer = context.getSharedPreferences(uid, Context.MODE_PRIVATE);
        String information = prefer.getString(informationType, "");
        return information;
    }
}
