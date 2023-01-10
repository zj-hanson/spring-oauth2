package com.lightshell.oauth2.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KevinDong
 */
public class ResponseMessage {

    protected String code;
    protected String msg;
    protected Map<String, Object> extData;

    public ResponseMessage() {
        extData = new HashMap<>();
    }

    public ResponseMessage(String code, String msg) {
        this();
        this.code = code;
        this.msg = msg;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @return the extData
     */
    public Map<String, Object> getExtData() {
        return extData;
    }

}
