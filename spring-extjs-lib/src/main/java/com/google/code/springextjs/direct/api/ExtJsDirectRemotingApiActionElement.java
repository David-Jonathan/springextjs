package com.google.code.springextjs.direct.api;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:26:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingApiActionElement {

    private String name;
    private int len;
    private boolean formHandler;

    public ExtJsDirectRemotingApiActionElement(String name, int len, boolean formHandler) {
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
