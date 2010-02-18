/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.junit.remoting;

import static org.junit.Assert.*;

import java.util.Locale;
import com.google.code.springextjs.junit.remoting.mock.MockExtJsRemotingController;
import com.google.code.springextjs.junit.remoting.mock.MockExtJsRemotingHttpServletRequest;
import com.google.code.springextjs.remoting.bean.ExtJsDirectRemotingResponseBean;
import com.google.code.springextjs.remoting.exceptions.UnAnnotatedRemoteMethodException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;


/**
 *
 * @author mansari
 */


public class ExtJsRemotingControllerTest{

    private MockExtJsRemotingController controller;

    private MockHttpServletResponse response;

    private String singleMethodRequestWithNoParamString = "{\"action\":\"Remoting\",\"method\":\"doWork1\",\"data\":null,\"type\":\"rpc\",\"tid\":2}";
    private MockExtJsRemotingHttpServletRequest singleMethodRequestWithNoParam;

    private String singleMethodRequestWithParamString = "{\"action\":\"Remoting\",\"method\":\"doWork2\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";
    private MockExtJsRemotingHttpServletRequest singleMethodRequestWithParam;

    private String singleMethodRequestWithParamNotAnnotatedString = "{\"action\":\"Remoting\",\"method\":\"doWork3\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";
    private MockExtJsRemotingHttpServletRequest singleMethodRequestWithParamNotAnnotated;

    private String multiMethodRequestWithParamString = "[{\"action\":\"Remoting\",\"method\":\"doWork1\",\"data\":null,\"type\":\"rpc\",\"tid\":1},{\"action\":\"Remoting\",\"method\":\"doWork2\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}]";
    private MockExtJsRemotingHttpServletRequest multiMethodRequestWithParam;

    private String configRequestString = "{\"action\":\"AdminExtJsRemotingController\",\"method\":\"getConfig\",\"data\":null,\"type\":\"rpc\",\"tid\":2}";
    private MockExtJsRemotingHttpServletRequest configRequest;

    private String formHandlerRequestRequestString = "queueId=1&workgroupId=2&extMethod=testFormMethod";
    private MockExtJsRemotingHttpServletRequest formHandlerRequest;

    @Before public void setUp() {
        this.controller = new MockExtJsRemotingController();

        this.response = new MockHttpServletResponse();
        
        this.singleMethodRequestWithNoParam = new MockExtJsRemotingHttpServletRequest(singleMethodRequestWithNoParamString);
        this.singleMethodRequestWithParam = new  MockExtJsRemotingHttpServletRequest (singleMethodRequestWithParamString);
        this.singleMethodRequestWithParamNotAnnotated = new  MockExtJsRemotingHttpServletRequest (singleMethodRequestWithParamNotAnnotatedString);
        this.multiMethodRequestWithParam = new  MockExtJsRemotingHttpServletRequest (multiMethodRequestWithParamString);


        this.configRequest = new MockExtJsRemotingHttpServletRequest (configRequestString);
        this.formHandlerRequest = new MockExtJsRemotingHttpServletRequest (formHandlerRequestRequestString);
    }

    @Test(expected=UnAnnotatedRemoteMethodException.class)
    public void testUnAnnotatedRemoteMethodException() throws Exception{
        controller.router(singleMethodRequestWithParamNotAnnotated, response, Locale.ENGLISH);
    }

    @Test
    public void testSingleValidRequestWithNoParams () throws Exception{
        ModelAndView mnv = controller.router(singleMethodRequestWithNoParam, response, Locale.ENGLISH);
        List<ExtJsDirectRemotingResponseBean> resps = (List<ExtJsDirectRemotingResponseBean>) mnv.getModel().get(mnv.getModel().keySet().iterator().next());
        
        assertTrue (resps.size() == 1);
        assertEquals (resps.get(0).getAction(), "Remoting");
        assertEquals (resps.get(0).getMethod(), "doWork1");
        assertEquals (resps.get(0).getTid(), 2);
        assertEquals (resps.get(0).getType(), "rpc");
    }

    @Test
    public void testSingleValidRequestWithParams () throws Exception{
        ModelAndView mnv = controller.router(singleMethodRequestWithParam, response, Locale.ENGLISH);
        List<ExtJsDirectRemotingResponseBean> resps = (List<ExtJsDirectRemotingResponseBean>) mnv.getModel().get(mnv.getModel().keySet().iterator().next());

        assertTrue (resps.size() == 1);

        assertEquals (resps.get(0).getAction(), "Remoting");
        assertEquals (resps.get(0).getMethod(), "doWork2");
        assertEquals (resps.get(0).getTid(), 2);
        assertEquals (resps.get(0).getType(), "rpc");
    }

    @Test
    public void testMultipleValidRequestWithNoParams () throws Exception{
        ModelAndView mnv = controller.router(multiMethodRequestWithParam, response, Locale.ENGLISH);
        List<ExtJsDirectRemotingResponseBean> resps = (List<ExtJsDirectRemotingResponseBean>) mnv.getModel().get(mnv.getModel().keySet().iterator().next());

        assertTrue (resps.size() == 2);

        assertEquals (resps.get(0).getAction(), "Remoting");
        assertEquals (resps.get(0).getMethod(), "doWork1");
        assertEquals (resps.get(0).getTid(), 1);
        assertEquals (resps.get(0).getType(), "rpc");

        assertEquals (resps.get(1).getAction(), "Remoting");
        assertEquals (resps.get(1).getMethod(), "doWork2");
        assertEquals (resps.get(1).getTid(), 2);
        assertEquals (resps.get(1).getType(), "rpc");
    }
}
