package com.ipcamera.demo.bean;

/*
 * 编写：vst  on 2017/10/30 17:22
 * //
 * //
 */
public class PushBindDeviceBean {
    private String uid;
    private String token;
    private String  oemid;
    private String encryp;
    private String ran;
    private String  date;



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEncryp() {
        return encryp;
    }

    public void setEncryp(String encryp) {
        this.encryp = encryp;
    }

    public String getOemid() {
        return oemid;
    }

    public void setOemid(String oemid) {
        this.oemid = oemid;
    }

    public String getRan() {
        return ran;
    }

    public void setRan(String ran) {
        this.ran = ran;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
