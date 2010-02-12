/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.junit.remoting;


import static org.junit.Assert.*;

import java.util.List;
import com.google.code.springextjs.junit.remoting.mock.MockExtJsRemotingController;
import com.google.code.springextjs.junit.remoting.mock.MockExtJsRemotingHttpServletRequest;
import com.google.code.springextjs.remoting.bean.ExtJsDirectRemotingRequestBean;
import com.google.code.springextjs.remoting.util.ExtJsRemotingUtil;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mansari
 */
public class ExtJsRemotingUtilTest {

    private String singleMethodRequestWithParamString = "{\"action\":\"Remoting\",\"method\":\"getConfig\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";
    private MockExtJsRemotingHttpServletRequest singleMethodRequestWithParam;

    //private MockExtJsRemotingController controller;
    
    @Before public void setUp() {
        //this.controller = new MockExtJsRemotingController ();
        singleMethodRequestWithParam = new  MockExtJsRemotingHttpServletRequest (singleMethodRequestWithParamString);
    }

    @Test
    public void testGetExtDirectRemotingRequestBeans (){
        List<ExtJsDirectRemotingRequestBean> extReqs = ExtJsRemotingUtil.getExtDirectRemotingRequestBeans(this.singleMethodRequestWithParam);
        assertEquals(1, extReqs.size());

        ExtJsDirectRemotingRequestBean extReq = extReqs.get(0);
        assertEquals("Remoting", extReq.getAction());
        assertEquals(3, extReq.getData().size());
        assertEquals("getConfig", extReq.getMethod());
        assertEquals(2, extReq.getTid());
        assertEquals("rpc", extReq.getType());

        assertEquals(Integer.class,extReq.getData().get(0).getClass());
        assertEquals(Double.class,extReq.getData().get(1).getClass());
        assertEquals(String.class,extReq.getData().get(2).getClass());
    }

    @Test
    public void testCreateExtRemotingApiString (){
        String api = ExtJsRemotingUtil.createExtRemotingApiString("www.google.com", MockExtJsRemotingController.class);
        System.out.print("API: " + api);
        assertNotNull (api);
    }

}
