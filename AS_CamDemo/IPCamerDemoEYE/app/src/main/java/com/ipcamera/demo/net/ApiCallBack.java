package com.ipcamera.demo.net;


import com.ipcamera.demo.bean.ErrorBean;
import com.ipcamera.demo.bean.JsonBean;

/*
 * 编写：vst  on 2017/9/28 18:21
 * //
 * //
 */
public abstract interface ApiCallBack {
    void onFinish(JsonBean bean);
    void onError(ErrorBean bean);
}
