/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.junit.remoting.util;

import com.google.code.springextjs.remoting.bean.ExtJsDirectRemotingRequestBean;
import com.google.code.springextjs.remoting.util.JsonUtil;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mansari
 */
public class JsonUtilTest {

    @Before public void setUp() {

    }

    @Test public void testSerializeObjectToJSONObject (){
        ExtJsDirectRemotingRequestBean req = new ExtJsDirectRemotingRequestBean ();
        req.setAction("testAction");
        req.setForm(true);
        req.setMethod("testMethod");
        req.setTid(1);
        req.setType("testType");

        String json = JsonUtil.serializeObjectToJson(req);
        System.out.println ("!!!!: " + json);
        
        assertTrue (true);
    }

}
