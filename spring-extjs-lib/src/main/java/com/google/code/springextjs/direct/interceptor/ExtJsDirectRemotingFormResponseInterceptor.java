package com.google.code.springextjs.direct.interceptor;

import com.google.code.springextjs.direct.ExtJsDirectRemotingResponse;
import com.google.code.springextjs.direct.controller.ExtJsDirectRemotingController;
import com.google.code.springextjs.direct.view.ExtJsDirectRemotingJacksonJsonView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:27:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingFormResponseInterceptor extends HandlerInterceptorAdapter {

    private static final Log log = LogFactory.getLog(ExtJsDirectRemotingFormResponseInterceptor.class);

    public ExtJsDirectRemotingFormResponseInterceptor() {
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
        if (ExtJsDirectRemotingController.class.isAssignableFrom(handler.getClass())){
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

        ExtJsDirectRemotingResponse extJsRemotingResponse = createExtJsDirectRemotingResponseFromRequest (request);

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
                    for (ObjectError objectError: bindingResult.getGlobalErrors()){
                        String message = objectError.getDefaultMessage();
                        if (messageSource != null){
                            if (objectError.getCodes() != null && objectError.getCodes().length > 0){
                                message = messageSource.getMessage(objectError.getCode(), objectError.getArguments(), locale);
                            }
                            else{
                                message = objectError.getDefaultMessage();    
                            }
                        }
                        errorMap.put(objectError.getObjectName(), message);    
                    }
                }
                isSuccess = errorMap.isEmpty();
                if (!isSuccess)
                    resultMap.put("errors", errorMap);

            }
        }
        resultMap.put("success", isSuccess);
        extJsRemotingResponse.setResult(resultMap);

        modelAndView.setView(new ExtJsDirectRemotingJacksonJsonView());
        modelAndView.addObject(extJsRemotingResponse);

        return modelAndView;
    }

    private static ExtJsDirectRemotingResponse createExtJsDirectRemotingResponseFromRequest (HttpServletRequest request){

        ExtJsDirectRemotingResponse extJsRemotingResponse = new ExtJsDirectRemotingResponse ();
        extJsRemotingResponse.setAction(request.getParameter("extAction"));
        extJsRemotingResponse.setMethod(request.getParameter("extMethod"));
        extJsRemotingResponse.setType(request.getParameter("extType"));
        extJsRemotingResponse.setTid(Integer.parseInt(request.getParameter("extTID")));

        return extJsRemotingResponse;
    }

    /**
     *
     * @param models
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
                    //todo: should be !ExtJsDirectRemotingResponse.class.isAssignableFrom(bindingResult.getTarget().getClass())
                    if (!bindingResult.getTarget().getClass().isAssignableFrom(ExtJsDirectRemotingResponse.class))
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