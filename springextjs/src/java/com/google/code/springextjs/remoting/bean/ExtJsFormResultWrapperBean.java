/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.bean;

/**
 *
 * @author mansari
 */
public class ExtJsFormResultWrapperBean {

    private Object data;
    private boolean success;

    public ExtJsFormResultWrapperBean() {
    }

    public ExtJsFormResultWrapperBean(Object data, boolean success) {
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
