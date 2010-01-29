/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.springextjs.junit.remoting;


import java.util.Locale;
import net.sf.springextjs.junit.remoting.mock.MockExtJsRemotingController;
import net.sf.springextjs.junit.remoting.mock.MockExtJsRemotingHttpServletRequest;
import net.sf.springextjs.remoting.exceptions.UnAnnotatedRemoteMethodException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


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
        this.configRequest = new MockExtJsRemotingHttpServletRequest (configRequestString);
        this.formHandlerRequest = new MockExtJsRemotingHttpServletRequest (formHandlerRequestRequestString);
    }

    @Test
    public void testRouter () throws Exception{
        System.out.println ("Running: testRouter");
        /*String responseJson = controller.router(singleMethodRequestWithNoParam, response);
        JSONObject responseJsonObj = JsonLibUtil.serializeObjectToJSONObject(responseJson);
        assertEquals("doWork1.called", responseJsonObj.get("result"));
        //System.out.println ("Response: " + responseJson);

        responseJson = controller.router(singleMethodRequestWithParam, response);
        responseJsonObj = JsonLibUtil.serializeObjectToJSONObject(responseJson);
        assertEquals("doWork2.called", responseJsonObj.get("result"));

        responseJson = controller.router(configRequest, response);
        responseJsonObj = JsonLibUtil.serializeObjectToJSONObject(responseJson);
        assertEquals("getConfig.called", responseJsonObj.get("result"));

        //formHandlerRequest.setParameter("extMethod", "testFormMethod");
        String forwardPath = controller.router(formHandlerRequest, response);
        assertEquals("forward:/testFormMethod", forwardPath);*/
        
        //ModelAndView modelAndView = controller.router(singleMethodRequestWithNoParam, response);
        //JSONObject responseJsonObj = JsonLibUtil.serializeObjectToJSONObject(responseJson);
        //assertEquals("doWork1.called", responseJsonObj.get("result"));
    }

    @Test(expected=UnAnnotatedRemoteMethodException.class)
    public void testUnAnnotatedRemoteMethodException() throws Exception{
        System.out.println ("Running: testUnAnnotatedRemoteMethodException");
        controller.router(singleMethodRequestWithParamNotAnnotated, response, Locale.ENGLISH);
    }
}
