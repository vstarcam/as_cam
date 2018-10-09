package com.ipcamera.demo.utils;

import android.content.Context;
import android.widget.Toast;

import com.ipcamer.demo.R;

/*
 * 编写：wenlong on 2017/11/9 09:19
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class ToastUtils {
    public static void show(final Context context,final String string){
        Toast.makeText(context, string,Toast.LENGTH_LONG).show();
    }
}
