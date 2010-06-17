package com.google.code.springextjs.direct.api;

import com.google.code.springextjs.direct.annotations.ExtJsDirectRemotingAnnotations;
import com.google.code.springextjs.direct.controller.ExtJsDirectRemotingController;
import com.google.code.springextjs.direct.json.JacksonJsonMapperUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:29:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingApiUtil {

    private static final Log log = LogFactory.getLog(ExtJsDirectRemotingApiUtil.class);

    private static final Map<String,String> REMOTING_API_MAP = new HashMap<String,String>();

    private static final String EXT_DIRECT_REMOTING_TYPE = "remoting";
    private static final String EXT_DIRECT_POLLING_TYPE = "polling";

    public enum SupportedParamTypes {

        ServletRequest (ServletRequest.class),
        ServletResponse (ServletResponse.class),
        Locale (Locale.class);

        private Class clazz;

        SupportedParamTypes (Class clazz){
            this.clazz = clazz;
        }

        public Class getSupportedClass(){
            return this.clazz;
        }

        public static boolean isSupported (Class clazz){
            for (SupportedParamTypes st: SupportedParamTypes.values()){
                if (st.getSupportedClass().isAssignableFrom(clazz))
                    return true;
            }
            return false;
        }


    }
    /**
     * See createExtRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz, String remotingActionName)
     * @param remoteServiceUrl
     * @param clazz
     * @return
     */
    public static String getExtDirectRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsDirectRemotingController> clazz){
        return createExtRemotingApiStringFromClass (remoteServiceUrl, clazz, null);
    }

    /**
     * This method creates the ExtJS remoting API JSON needed to initialize the
     * the ExtJS remoting in the browser. The API is cached internally once \
     * generated to increase method performance.
     * @param remoteServiceUrl The URL of the router implemented by a Spring MVC
     * Controller.
     * @param clazz The ExtJsRemotingController implementation which has the
     * annotated methods to be called the ExtJS remoting engine running in
     * the client's browser.
     * @param remotingActionName The name to reference the generated remoting
     * API object by ExtJS remoting. Used in the browser when initializing ExtJS
     * remoting API (Example: var remotingProvider = new Ext.direct.RemotingProvider (MyRemotingActionName);).
     * If this is null the Class name of the clazz parameter is used for the generated action name.
     * @return JSON String ExtJS remoting API object.
     */
    public static String getExtDirectRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsDirectRemotingController> clazz, String remotingActionName){
        return createExtRemotingApiStringFromClass (remoteServiceUrl, clazz, remotingActionName);
    }



    private static String createExtRemotingApiStringFromClass (String remoteServiceUrl, Class<? extends ExtJsDirectRemotingController> clazz, String remotingActionName){

        //use class name if the remotingActionName is null
        if (remotingActionName == null){
            remotingActionName = clazz.getName();
            if (remotingActionName.lastIndexOf(".") >= 0){
                remotingActionName = remotingActionName.substring(remotingActionName.lastIndexOf(".")+1, remotingActionName.length());
            }
        }

        String apiString = REMOTING_API_MAP.get(clazz.getName());
        if (apiString == null){

            List<ExtJsDirectRemotingApiActionElement> apiBeans = new ArrayList<ExtJsDirectRemotingApiActionElement>();

            Map<String, List<ExtJsDirectRemotingApiActionElement>> map = new HashMap<String, List<ExtJsDirectRemotingApiActionElement>>();

            Method[] methods = clazz.getMethods();

            if (methods != null && methods.length > 0){
                for (Method method : methods){
                    if (method.isAnnotationPresent(ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod.class)){
                        int paramLength = calculateParamLength(method.getParameterTypes());
                        boolean isForm = false;
                        if (method.isAnnotationPresent(RequestMapping.class)){
                            RequestMethod[] requestMethods = method.getAnnotation(RequestMapping.class).method();
                            if (requestMethods != null && requestMethods.length > 0){
                                isForm = requestMethods[0].equals(RequestMethod.POST);
                            }
                        }
                        ExtJsDirectRemotingApiActionElement apiElement = new ExtJsDirectRemotingApiActionElement (method.getName(), paramLength, isForm);
                        apiBeans.add(apiElement);
                    }
                }
            }


            map.put(remotingActionName, apiBeans);
            apiString = createExtRemotingApiString (remotingActionName, remoteServiceUrl, map);
            REMOTING_API_MAP.put(clazz.getName(), apiString);
            log.info ("Remoting API added to cache: " + remotingActionName);
        }
        return apiString;
    }

    private static String createExtRemotingApiString (String apiName, String url, Map<String, List<ExtJsDirectRemotingApiActionElement>> remotingActionMap){
        ExtJsDirectRemotingApi api = new ExtJsDirectRemotingApi (url, EXT_DIRECT_REMOTING_TYPE, remotingActionMap);
        String output = apiName + " = " + JacksonJsonMapperUtil.serializeObjectToJson(api);
        return output;
    }

    private static int calculateParamLength (Class[] paramTypes){
        int counter = 0;
        if (paramTypes != null && paramTypes.length > 0){
            for (Class clazz: paramTypes){
                if (!SupportedParamTypes.isSupported(clazz))
                    counter++;
            }
        }
        return counter;
    }
}
