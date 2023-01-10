package com.lightshell.oauth2.common;

import java.util.List;

/**
 * @author KevinDong
 */
public class ResponseData<T> extends ResponseMessage {

    protected long count;
    protected List<T> data;

    public ResponseData() {

    }

    public ResponseData(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     * @return the data
     */
    public List<T> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<T> data) {
        this.data = data;
    }

}
