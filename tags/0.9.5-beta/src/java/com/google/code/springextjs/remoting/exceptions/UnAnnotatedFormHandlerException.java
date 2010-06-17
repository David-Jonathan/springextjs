/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.exceptions;

/**
 *
 * @author mansari
 */
public class UnAnnotatedFormHandlerException extends Exception{

    public UnAnnotatedFormHandlerException(Exception e) {
        super(e);
    }

    public UnAnnotatedFormHandlerException(String message) {
        super(message);
    }

}
