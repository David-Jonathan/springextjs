/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.exceptions;

/**
 *
 * @author mansari
 */
public class UnAnnotatedRemoteMethodException extends Exception{

    public UnAnnotatedRemoteMethodException(Exception e) {
        super(e);
    }

    public UnAnnotatedRemoteMethodException(String message) {
        super(message);
    }
}