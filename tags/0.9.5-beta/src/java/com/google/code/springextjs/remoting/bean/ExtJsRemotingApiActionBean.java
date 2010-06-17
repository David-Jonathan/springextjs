/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.bean;

/**
 *
 * @author mansari
 */
public class ExtJsRemotingApiActionBean {
    private String name;
    private int len;
    private boolean formHandler;

    public ExtJsRemotingApiActionBean(String name, int len, boolean formHandler) {
        this.name = name;
        this.len = len;
        this.formHandler = formHandler;
    }

    public boolean isFormHandler() {
        return formHandler;
    }

    public int getLen() {
        return len;
    }

    public String getName() {
        return name;
    }

}
