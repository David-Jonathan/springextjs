package com.google.code.springextjs.direct;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:24:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingRequest {

    private String action;
    private String method;
    private String type;
    private int tid;
    private Object[] data;
    private boolean form;

    public ExtJsDirectRemotingRequest (){

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isForm() {
        return form;
    }

    public void setForm(boolean form) {
        this.form = form;
    }

}
