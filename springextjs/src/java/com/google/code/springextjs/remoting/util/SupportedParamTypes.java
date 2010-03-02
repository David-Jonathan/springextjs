/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.util;

import java.util.Locale;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author mansari
 */
public enum SupportedParamTypes {

    ServletRequest (ServletRequest.class),
    ServletResponse (ServletResponse.class),
    Locale (Locale.class);

    private Class clazz;

    SupportedParamTypes (Class clazz){
        this.clazz = clazz;
    }

    public Class getSupportedClass(){
        return this.clazz;
    }

    public static boolean isSupported (Class clazz){
        for (SupportedParamTypes st: SupportedParamTypes.values()){
            if (st.getSupportedClass().isAssignableFrom(clazz))
                return true;
        }
        return false;
    }


}
