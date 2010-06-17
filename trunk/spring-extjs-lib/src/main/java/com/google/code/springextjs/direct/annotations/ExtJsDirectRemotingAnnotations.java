package com.google.code.springextjs.direct.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:28:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExtJsRemotingMethod {
        boolean formLoad () default false;
    }


}
