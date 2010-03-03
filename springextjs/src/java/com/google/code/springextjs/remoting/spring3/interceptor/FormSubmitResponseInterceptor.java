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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContext;

/**
 *
 * @author mansari
 *
 * This class is an interceptor for transforming a form processing MVC handlers
 * returning ModelAndView into an ExtJs direct remoting response.
 *
 * This class will use a configured "localeResolver" for determining the locale
 * of the request. It will also look for a "get" method which returns a
 * MessageSource to find any field error's corresponding localized message.
 * Success status for the form submit is determined by 
 */
public class FormSubmitResponseInterceptor extends HandlerInterceptorAdapter{

    private static final Log log = LogFactory.getLog(FormSubmitResponseInterceptor.class);
    
    public FormSubmitResponseInterceptor() {
    }

    /**
     * This method transforms the form handlers returned ModelAndView into
     * one which can be translated into JSON response which ExtJS direct
     * hndlers in the browser can understand.
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        if (ExtJsRemotingController.class.isAssignableFrom(handler.getClass())){
            if (isValidFormSubmit(request, handler))
                modelAndView = transformModelAndView (request, handler, modelAndView);
        }
    }

    private static boolean isValidFormSubmit (HttpServletRequest request, Object handler){

        String formSubmitMethodName = request.getParameter("extMethod");
        if (formSubmitMethodName != null){
            Method[] methods = handler.getClass().getDeclaredMethods();
            if (methods != null && methods.length > 0){
                for (Method method: methods){
                    if (method.getName().equals(formSubmitMethodName.trim())){
                        RequestMapping requestMappingAnnotation =  method.getAnnotation(RequestMapping.class);
                        String mappingVal = requestMappingAnnotation.value()[0];
                        if (mappingVal != null)
                            return (request.getPathInfo().contains(mappingVal));
                    }
                }
            }
        }
        return false;

    }

    private static ModelAndView transformModelAndView (HttpServletRequest request, Object handler, ModelAndView modelAndView){

        Map<String,Object> resultMap = new HashMap<String,Object>();
        boolean isSuccess = true;

        ExtJsDirectRemotingResponseBean extJsRemotingResponse = createExtJsDirectRemotingResponseFromRequest (request);

        if (modelAndView != null){

            RequestContext requestContext = new RequestContext (request);

            MessageSource messageSource = requestContext.getMessageSource();
            Locale locale = requestContext.getLocale();

            Map<String, Object> models = modelAndView.getModel();
            
            List<BindingResult> bindingResults  = getBindingResults (models);

            if ((models != null) && !models.isEmpty()){
                for (String key: models.keySet()){
                    if (models.get(key) != null)
                        resultMap.put(key, models.get(key));//extract all other models which will get serialized in the json response
                }
            }

            modelAndView.getModelMap().clear();

            if (bindingResults != null && !bindingResults.isEmpty()){
                Map<String, String> errorMap = new HashMap<String,String>();
                for (BindingResult bindingResult: bindingResults){

                    for (FieldError fieldError : bindingResult.getFieldErrors()){
                        String message = fieldError.getDefaultMessage();
                        if (messageSource != null){
                            message = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), locale);
                        }
                        errorMap.put(fieldError.getField(), message);
                   }
                }
                isSuccess = errorMap.isEmpty();
                if (!isSuccess)
                    resultMap.put("errors", errorMap);
                
            }
        }
        resultMap.put("success", isSuccess);
        extJsRemotingResponse.setResult(resultMap);

        modelAndView.setView(new ExtJsRemotingJacksonJsonView());
        modelAndView.addObject(extJsRemotingResponse);

        return modelAndView;
    }

    private static ExtJsDirectRemotingResponseBean createExtJsDirectRemotingResponseFromRequest (HttpServletRequest request){

        ExtJsDirectRemotingResponseBean extJsRemotingResponse = new ExtJsDirectRemotingResponseBean ();
        extJsRemotingResponse.setAction(request.getParameter("extAction"));
        extJsRemotingResponse.setMethod(request.getParameter("extMethod"));
        extJsRemotingResponse.setType(request.getParameter("extType"));
        extJsRemotingResponse.setTid(Integer.parseInt(request.getParameter("extTID")));

        return extJsRemotingResponse;
    }

    /**
     *
     * @param modelAndView
     * @return
     */
    private static List<BindingResult> getBindingResults (Map<String, Object> models){
        List<BindingResult> bindingResults = new ArrayList<BindingResult>();
        
        if (models != null && !models.isEmpty()){

            Set<String> keysToRemove = new HashSet<String>();//need to store in set to remove object later to avoid ConcurrentModificationException

            //extracts binding results
            for (String key : models.keySet()){
                Object model = models.get(key);
                if ((model != null) && (BindingResult.class.isAssignableFrom(model.getClass()))){
                    BindingResult bindingResult = (BindingResult) models.get(key);
                    keysToRemove.add(key);
                    if (!bindingResult.getTarget().getClass().isAssignableFrom(ExtJsDirectRemotingResponseBean.class))
                        bindingResults.add(bindingResult);
                }
            }

            //finds key for binding result target objects
            for (BindingResult bindingResult: bindingResults){
                Object target = bindingResult.getTarget();
                for (String key : models.keySet()){
                    Object model = models.get(key);
                    if (model != null && (model == target)){
                        keysToRemove.add(key);
                    }
                }

            }

            //removes binding result and target objects from modelandview map
            for (String key: keysToRemove){
                models.remove(key);
            }
            
        }
        
        return bindingResults;
    }   
}
