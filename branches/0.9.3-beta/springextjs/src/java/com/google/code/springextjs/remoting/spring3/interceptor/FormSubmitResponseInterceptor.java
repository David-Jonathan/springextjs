/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.spring3.interceptor;

import com.google.code.springextjs.remoting.bean.ExtJsDirectRemotingResponseBean;
import com.google.code.springextjs.remoting.spring3.controller.ExtJsRemotingController;
import com.google.code.springextjs.remoting.spring3.view.ExtJsRemotingJacksonJsonView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author mansari
 */
public class FormSubmitResponseInterceptor extends HandlerInterceptorAdapter{

    private static final Log log = LogFactory.getLog(FormSubmitResponseInterceptor.class);

    private static final String MAIN_MESSAGE_KEY = "message";
    
    public FormSubmitResponseInterceptor() {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        if (handler.getClass().isAssignableFrom(ExtJsRemotingController.class)){

            Map<String,Object> resultMap = new HashMap<String,Object>();
            boolean isSuccess = true;

            ExtJsDirectRemotingResponseBean extJsRemotingResponse = new ExtJsDirectRemotingResponseBean ();
            extJsRemotingResponse.setAction(request.getParameter("extAction"));
            extJsRemotingResponse.setMethod(request.getParameter("extMethod"));
            extJsRemotingResponse.setType(request.getParameter("extType"));
            extJsRemotingResponse.setTid(Integer.parseInt(request.getParameter("extTID")));

            if (modelAndView != null){

                //try and get message source
                //try get locale for request
                MessageSource messageSource = getMessageSource (handler);
                Locale locale = getLocale (request);
                
                String mainMessage = (String) resultMap.get(MAIN_MESSAGE_KEY);
                if (mainMessage != null && !mainMessage.isEmpty())
                    resultMap.put(MAIN_MESSAGE_KEY, mainMessage);
                
                //get binding result by getting all BindingResult objects whose target is not ExtJsRemootingResponseBean
                List<BindingResult> bindingResults  = getBindingResults (modelAndView);
                if (bindingResults != null && !bindingResults.isEmpty()){
                    Map<String, String> errorMap = new HashMap<String,String>();
                    for (BindingResult bindingResult: bindingResults){
                         //if any binding result has error, then success = false
                        for (FieldError fieldError : bindingResult.getFieldErrors()){
                            String message = fieldError.getDefaultMessage();
                            if (messageSource != null){
                                message = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), locale);
                            }
                            if (message == null)
                                message = "Field has error.";
                            errorMap.put(fieldError.getField(), message);
                       }
                    }
                    resultMap.put("errors", errorMap);
                    isSuccess = errorMap.isEmpty();
                }
            }
            extJsRemotingResponse.setSuccess(isSuccess);
            extJsRemotingResponse.setResult(resultMap);

            modelAndView =  new ModelAndView (new ExtJsRemotingJacksonJsonView());
            modelAndView.addObject(extJsRemotingResponse);
        }
    }

    protected static Locale getLocale (HttpServletRequest request){
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        LocaleResolver localeResolver = (LocaleResolver) context.getBean("localeResolver");
        if (localeResolver != null)
            return localeResolver.resolveLocale(request);
        else 
            return Locale.US;
    }

    private static final List<BindingResult> getBindingResults (ModelAndView modelAndView){
        List<BindingResult> bindingResults = new ArrayList<BindingResult>();
        if (modelAndView != null){
            Map<String, Object> models = modelAndView.getModel();
            if (models != null && !models.isEmpty()){
                for (String key : models.keySet()){
                    Object model = models.get(key);
                    if (model.getClass().isAssignableFrom(BindingResult.class)){
                        BindingResult bindingResult = (BindingResult) model;
                        if (!bindingResult.getTarget().getClass().isAssignableFrom(ExtJsDirectRemotingResponseBean.class))
                            bindingResults.add(bindingResult);
                    }
                }
            }
        }
        return bindingResults;
    }
    
    private static final MessageSource getMessageSource (Object handler)
    {
        Method[] methods = handler.getClass().getMethods();
        try{
            if (methods != null && methods.length > 0){
                for (Method method: methods){
                    if ((method.getReturnType() != null)
                            && (MessageSource.class.isAssignableFrom(method.getReturnType()))
                            && (method.getParameterTypes().length == 0))
                        return (MessageSource) method.invoke(handler);
                }
            }
        }
        catch (Exception e){
            log.error("Error: " + e,e);
        }
        return null;
    }
}
