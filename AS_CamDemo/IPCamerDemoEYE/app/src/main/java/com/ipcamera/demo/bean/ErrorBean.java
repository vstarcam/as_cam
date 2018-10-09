package com.ipcamera.demo.bean;

/*
 * 编写：wenlong on 2017/9/29 10:15
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class ErrorBean {
    private int errorCode;
    private String errorMsg;

    public ErrorBean(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "ErrorBean{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
