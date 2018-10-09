package com.ipcamera.demo.bean;

/*
 * 编写：wenlong on 2017/11/2 15:21
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class SetLanguageBean {
    private String uid;
    private String token;
    private String  oemid;
    private String encryp;
    private String ran;
    private String  date;
    private String language;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
