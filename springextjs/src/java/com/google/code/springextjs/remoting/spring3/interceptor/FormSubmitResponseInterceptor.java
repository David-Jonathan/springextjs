/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.spring3.interceptor;

import com.google.code.springextjs.remoting.spring3.controller.ExtJsRemotingController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author mansari
 */
public class FormSubmitResponseInterceptor extends HandlerInterceptorAdapter{

    public FormSubmitResponseInterceptor() {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        if (ExtJsRemotingController.class.isAssignableFrom(handler.getClass())){
            if (modelAndView != null){
                
                //try get locale for request
                //try and get message source

                //get binding result by getting all BindingResult objects whose target is not ExtJsRemootingResponseBean
                //if any binding result has error, then success = false

                //if ! success then extract field errors
                //do reflection on handler to find messagesource
                //if message source, then load field errors, else out defualt message for field error

                //highjack view with custom jackson view
                //look for message in model and view for root message
            }
        }
    }


}
