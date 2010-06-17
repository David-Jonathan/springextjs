package com.google.code.springextjs.direct.json;

import com.google.code.springextjs.direct.ExtJsDirectRemotingRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 19, 2010
 * Time: 8:57:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class JacksonJsonMapperUtilTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testSerializeObjectToJSONObject (){
        ExtJsDirectRemotingRequest req = new ExtJsDirectRemotingRequest ();
        req.setAction("testAction");
        req.setForm(true);
        req.setMethod("testMethod");
        req.setTid(1);
        req.setType("testType");

        String json = JacksonJsonMapperUtil.serializeObjectToJson(req);

        assertTrue (json != null);

        req = (ExtJsDirectRemotingRequest) JacksonJsonMapperUtil.deserializeJsonToObject(json, ExtJsDirectRemotingRequest.class);

        assertTrue (req != null);

        assertEquals ("testAction", req.getAction());
        assertEquals (true, req.isForm());
        assertEquals ("testMethod", req.getMethod());
        assertEquals (1, req.getTid());
        assertEquals ("testType", req.getType());
    }
}
