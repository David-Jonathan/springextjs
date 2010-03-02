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
import com.google.code.springextjs.remoting.util.ExtJsDirectRemotingApiUtil;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mansari
 */
public class ExtJsRemotingUtilTest {

    private String singleMethodRequestWithParamString = "{\"action\":\"Remoting\",\"method\":\"getConfig\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";
    private MockExtJsRemotingHttpServletRequest singleMethodRequestWithParam;

    
    @Before public void setUp() {
        singleMethodRequestWithParam = new  MockExtJsRemotingHttpServletRequest (singleMethodRequestWithParamString);
    }

    @Test
    public void testCreateExtRemotingApiString (){
        String api = ExtJsDirectRemotingApiUtil.getExtDirectRemotingApiString("www.google.com", MockExtJsRemotingController.class);
        assertNotNull (api);
    }

}
