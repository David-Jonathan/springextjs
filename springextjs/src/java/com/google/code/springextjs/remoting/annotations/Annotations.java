/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author mansari
 */
public class Annotations {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExtJsRemotingMethod {
        boolean formLoad () default false;
    }
}