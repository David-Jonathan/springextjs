package com.google.code.springextjs.direct.controller;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 8, 2010
 * Time: 8:12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnAnnotatedRemoteMethodException extends Exception{

    public UnAnnotatedRemoteMethodException(Exception e) {
        super(e);
    }

    public UnAnnotatedRemoteMethodException(String message) {
        super(message);
    }
}
