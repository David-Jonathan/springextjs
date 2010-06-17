package com.google.code.springextjs.direct.controller;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 8, 2010
 * Time: 8:12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnAnnotatedFormHandlerException extends Exception{

    public UnAnnotatedFormHandlerException(Exception e) {
        super(e);
    }

    public UnAnnotatedFormHandlerException(String message) {
        super(message);
    }
}
