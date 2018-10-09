package com.ipcamera.demo.net;


import okhttp3.Request;

public abstract class BaseCallback {


    /*public Type mType;

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }*/


    public BaseCallback() {
       // mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 请求前，加载圈可以写在这里
     * @param request
     */
    //public abstract void onBeforeRequest(Request request);


    /**
     * 请求失败
     * @param request
     * @param e
     */
    public abstract void onFailure(Request request, Exception e);


    /**
     * 请求成功时调用此方法
     *
     * @param json
     */
    public abstract void onResponse(int code,String json);
}