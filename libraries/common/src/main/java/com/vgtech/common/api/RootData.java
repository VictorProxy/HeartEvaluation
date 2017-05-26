package com.vgtech.common.api;

public class RootData extends AbsApiData {

    public boolean result;
    public int code;
    public String message;
    public String responce;
    public boolean isSuccess() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }
}
