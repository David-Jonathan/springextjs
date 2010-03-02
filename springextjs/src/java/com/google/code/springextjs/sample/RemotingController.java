/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.sample;

import com.google.code.springextjs.remoting.annotations.Annotations.ExtJsRemotingMethod;
import com.google.code.springextjs.remoting.spring3.controller.ExtJsRemotingController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author mansari
 */
@Controller
@RequestMapping(value = "/extjs/remoting")
public class RemotingController extends ExtJsRemotingController{

    public RemotingController() {
    }

    @ExtJsRemotingMethod
    public Long multiply (String num){
        return Long.valueOf(num)*8;
    }

    @ExtJsRemotingMethod
    public String doEcho (String message){
        return message;
    }

}
