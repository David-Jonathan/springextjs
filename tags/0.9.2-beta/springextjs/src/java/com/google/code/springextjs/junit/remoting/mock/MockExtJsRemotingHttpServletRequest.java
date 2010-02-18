/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.junit.remoting.mock;

import java.io.BufferedReader;
import java.io.StringReader;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author mansari
 */
public class MockExtJsRemotingHttpServletRequest extends MockHttpServletRequest{

    private final String extRemotingRequestString;

    public MockExtJsRemotingHttpServletRequest(String extRemotingRequestString) {
        this.extRemotingRequestString = extRemotingRequestString;
    }

    public BufferedReader getReader(){
        return new BufferedReader(new StringReader(this.extRemotingRequestString));
    }

}
