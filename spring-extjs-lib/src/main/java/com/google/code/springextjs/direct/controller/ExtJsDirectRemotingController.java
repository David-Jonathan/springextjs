/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.direct.controller;

import com.google.code.springextjs.direct.ExtJsDirectRemotingFormLoadDataWrapper;
import com.google.code.springextjs.direct.ExtJsDirectRemotingRequest;
import com.google.code.springextjs.direct.ExtJsDirectRemotingResponse;
import com.google.code.springextjs.direct.annotations.ExtJsDirectRemotingAnnotations;
import com.google.code.springextjs.direct.api.ExtJsDirectRemotingApiUtil;
import com.google.code.springextjs.direct.json.JacksonJsonMapperUtil;
import com.google.code.springextjs.direct.view.ExtJsDirectRemotingJacksonJsonView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 1, 2010
 * Time: 9:38:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingController {


    private static final Log log = LogFactory.getLog(ExtJsDirectRemotingController.class);

    public ExtJsDirectRemotingController() {
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
     * This is main router method with default RequestMapping
     * 
     * /router.
     * This is main entry point for all requests form and non form submits using
     * ExtJS remoting.
     * @param request
     * @param response
     * @param locale
     * @return
     * @throws UnAnnotatedRemoteMethodException
     */
    @RequestMapping(value="/router", method = RequestMethod.POST)
    public final ModelAndView router(HttpServletRequest request,HttpServletResponse response, Locale locale) throws UnAnnotatedRemoteMethodException {

        List<ExtJsDirectRemotingRequest> extRemotingRequests = getExtDirectRemotingRequests (request);
        
        List<ExtJsDirectRemotingResponse> extJsRemotingResponses = new ArrayList<ExtJsDirectRemotingResponse>();
        for (ExtJsDirectRemotingRequest extReq : extRemotingRequests){
            ExtJsDirectRemotingResponse extJsRemotingResponse = createBaseResponse(extReq);
            try{

                if (log.isDebugEnabled())
                    log.debug("Method fired: " + extReq.getMethod());

                if (extReq.isForm()){
                    String forwardPath = getForwardPathForFormMethod (this, extReq, request);
                    if (log.isDebugEnabled())
                        log.debug("Form forward path: " + forwardPath);
                    
                    return new ModelAndView("forward:" + forwardPath);
                }
                else{
                    Object result = processRemotingRequest (request, response, locale, this, extReq);

                    if (result != null && isFormLoadMethod(this, extReq))
                        result = getFormLoadObject(result);//if form load wrap in special form load object

                    extJsRemotingResponse.setResult(result);
                }
            }
            catch (UnAnnotatedRemoteMethodException e){
                log.error("Error: " + e,e);
                throw e;
            }
            catch (Exception e){
                log.error("Error on method: " + (extReq != null ? extReq.getMethod() : "not available") ,e);

                if (log.isDebugEnabled()){
                    extJsRemotingResponse.setType("exception");
                    extJsRemotingResponse.setMessage(e.getMessage());
                    extJsRemotingResponse.setWhere(e.getMessage());
                }
                else
                     extJsRemotingResponse.setMessage(e.getMessage());
            }
            extJsRemotingResponses.add(extJsRemotingResponse);
        }
        if (extJsRemotingResponses.size() == 1)
            return responseModelAndView (extJsRemotingResponses.get(0));
        else
            return responseModelAndView (extJsRemotingResponses);
    }

    /**
     * This method returns a wrapper used to encapsulate the domain object which
     * pertains to a form in ExtJS. The return object in any methods used to load forms
     * must be wrapped using this.
     * @param resultObject
     * @return
     */
    private static ExtJsDirectRemotingFormLoadDataWrapper getFormLoadObject (Object resultObject){
        return new ExtJsDirectRemotingFormLoadDataWrapper (resultObject, true);
    }

    private static final ModelAndView responseModelAndView (Object resultObject){
        ModelAndView mnv =  new ModelAndView (new ExtJsDirectRemotingJacksonJsonView());
        mnv.addObject(resultObject);
        return mnv;
    }

    private static final String getForwardPathForFormMethod (ExtJsDirectRemotingController controller, ExtJsDirectRemotingRequest extReq,HttpServletRequest request) throws Exception{
        Method method = getCorrespondingJavaMethod (controller, extReq.getMethod());
        if (isExtJsRemotingFormHandlerMethod(method)){
            String forwardHandlerPathValue = getFormMethodRequestMappingAnnotationValue (method);
            if (forwardHandlerPathValue.charAt(0) == '/' && forwardHandlerPathValue.length() > 1){
                forwardHandlerPathValue = forwardHandlerPathValue.substring(1, forwardHandlerPathValue.length());
            }
            return forwardHandlerPathValue;
        }
        else{
            throw new UnAnnotatedFormHandlerException("Invalid remoting form method: " + extReq.getMethod() + ". Perhaps you forgot the RequestMapping.value annotation");
        }
    }

    private static final Object processRemotingRequest (HttpServletRequest request,HttpServletResponse response, Locale locale, ExtJsDirectRemotingController controller, ExtJsDirectRemotingRequest extReq) throws Exception{

        Method method = getCorrespondingJavaMethod (controller, extReq.getMethod());
        if (isExtJsRemotingMethod (method)){
            Class[] classes = method.getParameterTypes();
            Object[] params = new Object[0];
            if (classes != null && classes.length > 0){
                params = new Object[classes.length];
                int paramIndex = 0;
                int jsonParamIndex = 0;
                for (Class methodParamClass : classes){

                    if (ExtJsDirectRemotingApiUtil.SupportedParamTypes.isSupported(methodParamClass)){
                        if (ExtJsDirectRemotingApiUtil.SupportedParamTypes.ServletResponse.getSupportedClass().isAssignableFrom(methodParamClass)){
                            params[paramIndex] = response;
                        }
                        else if (ExtJsDirectRemotingApiUtil.SupportedParamTypes.ServletRequest.getSupportedClass().isAssignableFrom(methodParamClass)){
                            params[paramIndex] = request;
                        }
                        else if (ExtJsDirectRemotingApiUtil.SupportedParamTypes.Locale.getSupportedClass().isAssignableFrom(methodParamClass)){
                            params[paramIndex] = locale;
                        }
                    }
                    else if (extReq.getData() != null && extReq.getData().length > 0){
                        Object passedParamVal = extReq.getData()[jsonParamIndex];


                        if (!methodParamClass.isPrimitive()){//must be either string or object param
                            passedParamVal = JacksonJsonMapperUtil.serializeObjectToJson(passedParamVal);//serialize to json string first since Jackson mapper deserializes unknown objects to LinkedHashMap
                            if (!methodParamClass.getClass().equals(String.class))//must be object param
                                passedParamVal = JacksonJsonMapperUtil.deserializeJsonToObject(passedParamVal.toString(), methodParamClass);
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
            throw new UnAnnotatedRemoteMethodException ("Invalid remoting method: " + extReq.getMethod() + ". Perhaps you forgot to add the @ExtJsRemotingMethod annotation to your remoting method?");
    }

    private static final boolean isFormLoadMethod (ExtJsDirectRemotingController controller, ExtJsDirectRemotingRequest extReq) throws Exception{
        Method method = getCorrespondingJavaMethod (controller, extReq.getMethod());
        return method.getAnnotation(ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod.class).formLoad();
    }

    private static final Method getCorrespondingJavaMethod (ExtJsDirectRemotingController controller, String methodName) throws Exception{
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
        return method.getAnnotation(ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod.class) != null;
    }

    private static final boolean isExtJsRemotingFormHandlerMethod (Method method){
        return method.getAnnotation(RequestMapping.class) != null
               && method.getAnnotation(RequestMapping.class).value() != null
               && method.getAnnotation(RequestMapping.class).value().length > 0;
    }

    private static final String getFormMethodRequestMappingAnnotationValue (Method method){
        return method.getAnnotation(RequestMapping.class).value()[0];
    }

    private static final ExtJsDirectRemotingResponse createBaseResponse (ExtJsDirectRemotingRequest extReq){
        ExtJsDirectRemotingResponse extJsRemotingResponse = new ExtJsDirectRemotingResponse();
        if (extReq != null){
            extJsRemotingResponse.setAction(extReq.getAction());
            extJsRemotingResponse.setMethod(extReq.getMethod());
            extJsRemotingResponse.setTid(extReq.getTid());
            extJsRemotingResponse.setType(extReq.getType());
        }
        return extJsRemotingResponse;
    }

    /**
     * This method abstracts the http requet by transforming the raw
     * http request to one or more ExtJsDirectRemotingRequest's.
     * Returns list of ExtJs remoting requests.
     * @param request Http request
     * @return List of one or more ExtJsDirectRemotingRequest objects.
     *  In case of form submit using post, the list will contain only one object.
     */
    private static List<ExtJsDirectRemotingRequest> getExtDirectRemotingRequests (HttpServletRequest request){
        List<ExtJsDirectRemotingRequest> reqs = new ArrayList<ExtJsDirectRemotingRequest>();

        //must check if parameter passed since reading the raw request body can interfere with the state of the request object in further transactions
        String extPostRemoteMethod = request.getParameter("extMethod");
        if (extPostRemoteMethod != null){
            ExtJsDirectRemotingRequest extReq = new ExtJsDirectRemotingRequest();
            extReq.setMethod(extPostRemoteMethod);
            extReq.setForm(true);
            reqs.add(extReq);
        }
        else{//parse raw request body if not a post request
            String rawRequestString = null;
            try{
                rawRequestString = parseRawHttpRequest (request);
                //multiple ext js remoting requests sent in one http request
                if (rawRequestString.length() > 0 && rawRequestString.charAt(0) == '['){//should be an array, multiple batched requests likely
                    reqs.addAll(JacksonJsonMapperUtil.serializeJsonArrayToList(rawRequestString, new TypeReference<List<ExtJsDirectRemotingRequest>>() { }));
                }
                //only one extjs remoting sent with this http request
                else{
                    ExtJsDirectRemotingRequest extReq = (ExtJsDirectRemotingRequest) JacksonJsonMapperUtil.deserializeJsonToObject(rawRequestString, ExtJsDirectRemotingRequest.class);
                    reqs.add(extReq);
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

