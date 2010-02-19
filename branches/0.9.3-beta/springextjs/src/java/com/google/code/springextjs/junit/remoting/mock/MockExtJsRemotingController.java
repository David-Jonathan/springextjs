/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.junit.remoting.mock;

import com.google.code.springextjs.remoting.annotations.Annotations.ExtJsRemotingMethod;
import com.google.code.springextjs.remoting.spring3.controller.ExtJsRemotingController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author mansari
 */
@RequestMapping("/admin/extjs/remoting")
public class MockExtJsRemotingController extends ExtJsRemotingController{

    public MockExtJsRemotingController() {
    }

    @ExtJsRemotingMethod (paramLength = 0)
    public String getConfig(){
        System.out.println ("Testing: getConfig");
        return "getConfig.called";
    }

    @ExtJsRemotingMethod (paramLength = 0)
    public String doWork1 (){
        System.out.println ("Testing: doWork1");
        return "doWork1.called";
    }

    @ExtJsRemotingMethod(paramLength = 3)
    @RequestMapping (method = RequestMethod.POST)
    public String doWork2 (long i, Double d, String s){
        System.out.println ("Testing: doWork2");
        return "doWork2.called";
    }
    
    public String doWork3 (long i, Double d, String s){
        System.out.println ("Testing: doWork3");
        return "doWork3.called";
    }

    @ExtJsRemotingMethod(paramLength = 3)
    @RequestMapping (value="/testFormMethod", method = RequestMethod.POST)
    public void testFormMethod (String queueName, long workgroupId){

        System.out.println ("Testing: testFormMethod");
    }
}
