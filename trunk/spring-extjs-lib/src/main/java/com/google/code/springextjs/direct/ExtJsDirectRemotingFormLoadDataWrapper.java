package com.google.code.springextjs.direct;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:25:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingFormLoadDataWrapper {

    private Object data;
    private boolean success;

    public ExtJsDirectRemotingFormLoadDataWrapper() {
    }

    public ExtJsDirectRemotingFormLoadDataWrapper(Object data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



}
