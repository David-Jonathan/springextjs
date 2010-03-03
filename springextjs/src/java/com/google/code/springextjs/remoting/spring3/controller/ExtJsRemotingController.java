/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.spring3.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.code.springextjs.remoting.annotations.Annotations.ExtJsRemotingMethod;
import com.google.code.springextjs.remoting.bean.ExtJsDirectRemotingRequestBean;
import com.google.code.springextjs.remoting.bean.ExtJsDirectRemotingResponseBean;
import com.google.code.springextjs.remoting.bean.ExtJsFormResultWrapperBean;
import com.google.code.springextjs.remoting.exceptions.UnAnnotatedFormHandlerException;
import com.google.code.springextjs.remoting.exceptions.UnAnnotatedRemoteMethodException;
import com.google.code.springextjs.remoting.util.JsonUtil;
import com.google.code.springextjs.remoting.spring3.view.ExtJsRemotingJacksonJsonView;
import com.google.code.springextjs.remoting.util.ExtJsDirectRemotingApiUtil;
import com.google.code.springextjs.remoting.util.SupportedParamTypes;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author mansari
 */

public abstract class ExtJsRemotingController {

    private static final Log log = LogFactory.getLog(ExtJsRemotingController.class);

    public ExtJsRemotingController() {
    }

    /**
     * 
     * @param request
     * @param response
     * @param actionName
     * @return
     */
    @RequestMapping(value="/api/{actionName}", method = RequestMethod.GET)
    @ResponseBody
    public final String api (HttpServletRequest request,HttpServletResponse response, @PathVariable String actionName){
        
        response.setContentType("text/javascript");

        String routerPath = request.getPathInfo();
        routerPath = request.getContextPath() + routerPath.substring(0, routerPath.indexOf("/api")) + "/router";

        return ExtJsDirectRemotingApiUtil.getExtDirectRemotingApiString(routerPath, this.getClass(), actionName);
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/api", method = RequestMethod.GET)
    @ResponseBody
    public final String api (HttpServletRequest request,HttpServletResponse response){
        response.setContentType("text/javascript");

        String routerPath = request.getPathInfo();
        routerPath = request.getContextPath() + routerPath.substring(0, routerPath.indexOf("/api")) + "/router";

        return ExtJsDirectRemotingApiUtil.getExtDirectRemotingApiString(routerPath, this.getClass());
    }

    /**
     * This is main router method with defualt RequestMapping /router.
     * This is main entry point for all requests form and non form submits using
     * ExtJS remoting.
     * @param request
     * @param response
     * @param locale
     * @return
     * @throws UnAnnotatedRemoteMethodException
     */
    @RequestMapping(value="/router", method = RequestMethod.POST)
    public final ModelAndView router(HttpServletRequest request,HttpServletResponse response, Locale locale) throws UnAnnotatedRemoteMethodException{

        List<ExtJsDirectRemotingRequestBean> extRemotingRequests = getExtDirectRemotingRequestBeans (request);
        List<ExtJsDirectRemotingResponseBean> extJsRemotingResponses = new ArrayList<ExtJsDirectRemotingResponseBean>();
        for (ExtJsDirectRemotingRequestBean extReqBean : extRemotingRequests){
            ExtJsDirectRemotingResponseBean extJsRemotingResponse = createBaseResponse(extReqBean);
            try{

                if (log.isDebugEnabled())
                    log.debug("Method fired: " + extReqBean.getMethod());

                if (extReqBean.isForm()){
                    String forwardPath = getForwardPathForFormMethod (this, extReqBean, request);
                    if (log.isDebugEnabled())
                        log.debug("Form forward path: " + forwardPath);
                    
                    return new ModelAndView("forward:" + forwardPath);
                }
                else{
                    Object result = processRemotingRequest (request, response, locale, this, extReqBean);

                    if (result != null && isFormLoadMethod(this, extReqBean))
                        result = getFormLoadObject(result);//if form load wrap in special form load object
                    
                    extJsRemotingResponse.setResult(result);
                }
            }
            catch (UnAnnotatedRemoteMethodException e){
                log.error("Error: " + e,e);
                throw e;
            }
            catch (Exception e){
                log.error("Error on method: " + extReqBean.getMethod(),e);
                
                if (log.isDebugEnabled()){
                    extJsRemotingResponse.setType("exception");
                    extJsRemotingResponse.setMessage(e.getMessage());
                    extJsRemotingResponse.setWhere(e.getMessage());
                }
                else
                     extJsRemotingResponse.setMessage("ajax.server.error.message");
            }
            extJsRemotingResponses.add(extJsRemotingResponse);
        }
        
        return responseModelAndView (extJsRemotingResponses);
    }

    /**
     * This method returns a wrapper used to encapsulate the domain object which
     * pertains to a form in ExtJS. The return object in any methods used to load forms
     * must be wrapped using this.
     * @param resultObject
     * @return
     */
    private static ExtJsFormResultWrapperBean getFormLoadObject (Object resultObject){
        return new ExtJsFormResultWrapperBean (resultObject, true);
    }

    private static final ModelAndView responseModelAndView (Object resultObject){
        ModelAndView mnv =  new ModelAndView (new ExtJsRemotingJacksonJsonView());
        mnv.addObject(resultObject);
        return mnv;
    }

    private static final String getForwardPathForFormMethod (ExtJsRemotingController controller, ExtJsDirectRemotingRequestBean extReqBean,HttpServletRequest request) throws Exception{
        Method method = getCorrespondingJavaMethod (controller, extReqBean.getMethod());
        if (isExtJsRemotingFormHandlerMethod(method)){
            String forwardHandlerPathValue = getFormMethodRequestMappingAnnotationValue (method);
            if (forwardHandlerPathValue.charAt(0) == '/' && forwardHandlerPathValue.length() > 1){
                forwardHandlerPathValue = forwardHandlerPathValue.substring(1, forwardHandlerPathValue.length());
            }
            return forwardHandlerPathValue;
        }
        else{
            throw new UnAnnotatedFormHandlerException ("Invalid remoting form method: " + extReqBean.getMethod() + ". Perhaps you forgot the RequestMapping.value annotation");
        }
    }

    private static final Object processRemotingRequest (HttpServletRequest request,HttpServletResponse response, Locale locale, ExtJsRemotingController controller, ExtJsDirectRemotingRequestBean extReqBean) throws Exception{

        Method method = getCorrespondingJavaMethod (controller, extReqBean.getMethod());
        if (isExtJsRemotingMethod (method)){
            Class[] classes = method.getParameterTypes();
            Object[] params = new Object[0];
            if (classes != null && classes.length > 0){
                params = new Object[classes.length];
                int paramIndex = 0;
                int jsonParamIndex = 0;
                for (Class methodParamClass : classes){

                    if (SupportedParamTypes.isSupported(methodParamClass)){
                        if (SupportedParamTypes.ServletResponse.getSupportedClass().isAssignableFrom(methodParamClass)){
                            params[paramIndex] = response;
                        }
                        else if (SupportedParamTypes.ServletRequest.getSupportedClass().isAssignableFrom(methodParamClass)){
                            params[paramIndex] = request;
                        }
                        else if (SupportedParamTypes.Locale.getSupportedClass().isAssignableFrom(methodParamClass)){
                            params[paramIndex] = locale;
                        }
                    }
                    else if (extReqBean.getData() != null && extReqBean.getData().length > 0){
                        Object passedParamVal = extReqBean.getData()[jsonParamIndex];

                        
                        if (!methodParamClass.isPrimitive()){//must be either string or object param
                            passedParamVal = JsonUtil.serializeObjectToJson(passedParamVal);//serialize to json string first since Jackson mapper deserializes unknown objects to LinkedHashMap
                            if (!methodParamClass.getClass().equals(String.class))//must be object param
                                passedParamVal = JsonUtil.deserializeJsonToObject(passedParamVal.toString(), methodParamClass);
                        }
                        
                        jsonParamIndex++;
                        params[paramIndex] = passedParamVal;
                    }
                    else
                        throw new Exception ("Error, param mismatch. Please check your remoting method signature to ensure all supported param types are used.");
                    
                    paramIndex++;
                }
            }
            return method.invoke(controller, params);      
        }
        else
            throw new UnAnnotatedRemoteMethodException ("Invalid remoting method: " + extReqBean.getMethod() + ". Perhaps you forgot to add the @ExtJsRemotingMethod annotation to your remoting method?");
    }

    private static final boolean isFormLoadMethod (ExtJsRemotingController controller, ExtJsDirectRemotingRequestBean extReqBean) throws Exception{
        Method method = getCorrespondingJavaMethod (controller, extReqBean.getMethod());
        return method.getAnnotation(ExtJsRemotingMethod.class).formLoad();
    }

    private static final Method getCorrespondingJavaMethod (ExtJsRemotingController controller, String methodName) throws Exception{
        Class clazz = controller.getClass();
        Class[] paramTypes = new Class[0];
        for (Method m : clazz.getMethods()){
            if (m.getName().equals(methodName)){
                paramTypes = m.getParameterTypes();
                break;
            }
        }
        Method method = clazz.getMethod(methodName,paramTypes);
        return method;
    }

    private static final boolean isExtJsRemotingMethod (Method method){
        return method.getAnnotation(ExtJsRemotingMethod.class) != null;
    }

    private static final boolean isExtJsRemotingFormHandlerMethod (Method method){
        return method.getAnnotation(RequestMapping.class) != null
               && method.getAnnotation(RequestMapping.class).value() != null
               && method.getAnnotation(RequestMapping.class).value().length > 0;
    }

    private static final String getFormMethodRequestMappingAnnotationValue (Method method){
        return method.getAnnotation(RequestMapping.class).value()[0];
    }

    private static final ExtJsDirectRemotingResponseBean createBaseResponse (ExtJsDirectRemotingRequestBean extReqBean){
        ExtJsDirectRemotingResponseBean extJsRemotingResponse = new ExtJsDirectRemotingResponseBean();
        extJsRemotingResponse.setAction(extReqBean.getAction());
        extJsRemotingResponse.setMethod(extReqBean.getMethod());
        extJsRemotingResponse.setTid(extReqBean.getTid());
        extJsRemotingResponse.setType(extReqBean.getType());
        return extJsRemotingResponse;
    }

    /**
     * This method abstracts the http requet by transforming the raw
     * http request to one or more ExtJsDirectRemotingRequestBean's.
     * Returns list of ExtJs remoting requests.
     * @param request Http request
     * @return List of one or more ExtJsDirectRemotingRequestBean objects.
     *  In case of form submit using post, the list will contain only one object.
     */
    private static List<ExtJsDirectRemotingRequestBean> getExtDirectRemotingRequestBeans (HttpServletRequest request){
        List<ExtJsDirectRemotingRequestBean> reqs = new ArrayList<ExtJsDirectRemotingRequestBean>();

        //must check if parameter passed since reading the raw request body can interfere with the state of the request object in further transactions
        String extPostRemoteMethod = request.getParameter("extMethod");
        if (extPostRemoteMethod != null){
            ExtJsDirectRemotingRequestBean extReqBean = new ExtJsDirectRemotingRequestBean();
            extReqBean.setMethod(extPostRemoteMethod);
            extReqBean.setForm(true);
            reqs.add(extReqBean);
        }
        else{//parse raw request body if not a post request
            String rawRequestString = null;
            try{
                rawRequestString = parseRawHttpRequest (request);
                //multiple ext js remoting requests sent in one http request
                if (rawRequestString.length() > 0 && rawRequestString.charAt(0) == '['){//should be an array, multiple batched requests likely
                    reqs.addAll(JsonUtil.serializeJsonArrayToList(rawRequestString, new TypeReference<List<ExtJsDirectRemotingRequestBean>>() { }));
                }
                //only one extjs remoting sent with this http request
                else{
                    ExtJsDirectRemotingRequestBean extReqBean = (ExtJsDirectRemotingRequestBean) JsonUtil.deserializeJsonToObject(rawRequestString, ExtJsDirectRemotingRequestBean.class);
                    reqs.add(extReqBean);
                }
            }
            catch (Exception e){
                log.error("Error: " + e, e);
            }
        }
        return reqs;
    }

    private static String parseRawHttpRequest (HttpServletRequest request) throws Exception{

        Reader input = request.getReader();
        Writer output = new StringWriter ();
        char[] buffer = new char[1024 * 4];
        
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }

        String requestString = output.toString();
        return requestString;
    }
}
