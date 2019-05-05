package com.ipcamera.demo.bean;

/*
 * 编写：vst  on 2017/5/5 14:03
 * //
 * //
 */
public class JsonBean {
    private int code;
    private String json;

    public JsonBean(int code, String json) {
        this.code = code;
        this.json = json;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "JsonBean{" +
                "code=" + code +
                ", json='" + json + '\'' +
                '}';
    }
}
