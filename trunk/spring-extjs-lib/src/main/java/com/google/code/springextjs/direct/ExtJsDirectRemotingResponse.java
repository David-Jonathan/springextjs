package com.google.code.springextjs.direct;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:24:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingResponse {


    private int tid;
    private String action;
    private String method;
    private Object result;

    //debug fields
    private String type;
    private String message;
    private String where;

    public ExtJsDirectRemotingResponse() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

}

