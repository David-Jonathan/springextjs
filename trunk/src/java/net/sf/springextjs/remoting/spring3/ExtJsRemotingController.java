/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.springextjs.remoting.spring3;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.springextjs.remoting.annotations.Annotations.ExtJsRemotingMethod;
import net.sf.springextjs.remoting.util.ExtJsRemotingUtil;
import net.sf.springextjs.remoting.bean.ExtJsDirectRemotingRequestBean;
import net.sf.springextjs.remoting.bean.ExtJsDirectRemotingResponseBean;
import net.sf.springextjs.remoting.bean.ExtJsFormResultWrapperBean;
import net.sf.springextjs.remoting.exceptions.UnAnnotatedFormHandlerException;
import net.sf.springextjs.remoting.exceptions.UnAnnotatedRemoteMethodException;
import net.sf.springextjs.remoting.util.JsonLibUtil;
import net.sf.springextjs.remoting.view.ExtJsRemotingJacksonJsonView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author mansari
 */

public abstract class ExtJsRemotingController {

    private static final Log log = LogFactory.getLog(ExtJsRemotingController.class);
    
    public static final String STATUS_KEY = "status";
    public static final String SUCCESS_STATUS = "success";
    public static final String FAIL_STATUS = "fail";
    public static final String INVALID_SESSION_STATUS = "invalid-session";

    public ExtJsRemotingController() {
    }

    
    @RequestMapping(value="/router", method = RequestMethod.POST)
    public ModelAndView router(HttpServletRequest request,HttpServletResponse response, Locale locale) throws UnAnnotatedRemoteMethodException{

        List<ExtJsDirectRemotingRequestBean> extRemotingRequests = ExtJsRemotingUtil.getExtDirectRemotingRequestBeans (request);
        List<ExtJsDirectRemotingResponseBean> extJsRemotingResponses = new ArrayList<ExtJsDirectRemotingResponseBean>();
        for (ExtJsDirectRemotingRequestBean extReqBean : extRemotingRequests){
            ExtJsDirectRemotingResponseBean extJsRemotingResponse = createBaseResponse(extReqBean);
            try{
                log.info("Method fired: " + extReqBean.getMethod());
                if (extReqBean.isForm()){
                    String forwardPath = getForwardPathForFormMethod (this, extReqBean, request);
                    log.info("Form forward path: " + forwardPath);
                    return new ModelAndView("forward:" + forwardPath);
                }
                else{
                    Object result = processRemotingRequest (request, response, locale, this, extReqBean);
                    if (result == null)
                        result = new JSONObject ();
                    extJsRemotingResponse.setResult(result);
                }
            }
            catch (UnAnnotatedRemoteMethodException e){
                log.error("Error: " + e,e);
                throw e;
            }
            catch (Exception e){
                log.error("Error on method: " + extReqBean.getMethod(),e);
                
                extJsRemotingResponse.setSuccess(false);
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

    protected ModelAndView remotingFormPostModelAndView (HttpServletRequest request, Locale locale, MessageSource messageSource, boolean isSuccess, String mainMessage, BindingResult bindingResult){
        ExtJsDirectRemotingResponseBean extJsRemotingResponse = new ExtJsDirectRemotingResponseBean ();
        
        extJsRemotingResponse.setSuccess(isSuccess);
        extJsRemotingResponse.setAction(request.getParameter("extAction"));
        extJsRemotingResponse.setMethod(request.getParameter("extMethod"));
        extJsRemotingResponse.setType(request.getParameter("extType"));
        extJsRemotingResponse.setTid(Integer.parseInt(request.getParameter("extTID")));

        Map<String,Object> result = new HashMap<String,Object>();
        //process main message
        if (mainMessage != null && !mainMessage.isEmpty())
            result.put("message", mainMessage);
        
        result.put("success", isSuccess);
        if (bindingResult != null && bindingResult.hasFieldErrors()){
            Map<String, String> errorMap = new HashMap<String,String>();
            for (FieldError fieldError : bindingResult.getFieldErrors()){
                String message = fieldError.getDefaultMessage();
                if (messageSource != null){
                    locale = (locale != null ? locale : Locale.ENGLISH);
                    message = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), locale);
                }
                errorMap.put(fieldError.getField(), message);
           }
           result.put("errors", errorMap);//this must be present if issuccess == false   
        }

        extJsRemotingResponse.setResult(result);
        return responseModelAndView (extJsRemotingResponse);
    }

    protected ExtJsFormResultWrapperBean getFormLoadObject (Object resultObject){
        return new ExtJsFormResultWrapperBean (resultObject, true);
    }

    private static final ModelAndView responseModelAndView (Object resultObject){
        ModelAndView mnv =  new ModelAndView (new ExtJsRemotingJacksonJsonView());
        if (resultObject instanceof Collection)
            mnv.addObject(JsonLibUtil.serializeObjectToJSONArray(resultObject));//enforces transient rule if first serialized to JSOBObject
        else if (!(resultObject instanceof JSONObject))
            mnv.addObject(JsonLibUtil.serializeObjectToJSONObject(resultObject));//enforces transient rule if first serialized to JSOBObject
        else
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
                for (Class paramClass : classes){


                    if (ServletResponse.class.isAssignableFrom(paramClass)){
                        params[paramIndex] = response;
                    }
                    else if (ServletRequest.class.isAssignableFrom(paramClass)){
                        params[paramIndex] = request;
                    }
                    else if (Locale.class.isAssignableFrom(paramClass)){
                        params[paramIndex] = locale;
                    }

                    else if (extReqBean.getData() != null && extReqBean.getData().size() > 0){
                        Object paramVal = extReqBean.getData().get(jsonParamIndex);
                        if (paramClass.equals(String.class) && paramVal instanceof JSONObject){
                            paramVal = paramVal.toString();
                        }
                        else if (paramClass.equals(String.class) && paramVal instanceof JSONArray){
                            paramVal = paramVal.toString();
                        }
                        else if (!paramClass.equals(JSONObject.class) && paramVal instanceof JSONObject){
                             paramVal = JsonLibUtil.deserializeJSONObjectToObject((JSONObject) paramVal, paramClass);
                        }
                        jsonParamIndex++;
                        params[paramIndex] = paramVal;
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
}
