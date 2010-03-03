/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.util;



import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.code.springextjs.remoting.annotations.Annotations.ExtJsRemotingMethod;
import com.google.code.springextjs.remoting.bean.ExtJsRemotingApiActionBean;
import com.google.code.springextjs.remoting.bean.ExtJsRemotingApiBean;
import com.google.code.springextjs.remoting.spring3.controller.ExtJsRemotingController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author mansari
 */
public class ExtJsDirectRemotingApiUtil {

    private static final Log log = LogFactory.getLog(ExtJsDirectRemotingApiUtil.class);

    private static final Map<String,String> REMOTING_API_MAP = new HashMap<String,String>();

    private static final String EXT_DIRECT_REMOTING_TYPE = "remoting";
    private static final String EXT_DIRECT_POLLING_TYPE = "polling";

    /**
     * See createExtRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz, String remotingActionName)
     * @param remoteServiceUrl
     * @param clazz
     * @return
     */
    public static String getExtDirectRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz){
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
    public static String getExtDirectRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz, String remotingActionName){
        return createExtRemotingApiStringFromClass (remoteServiceUrl, clazz, remotingActionName);
    }

    
    
    private static String createExtRemotingApiStringFromClass (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz, String remotingActionName){

        //use class name if the remotingActionName is null
        if (remotingActionName == null){
            remotingActionName = clazz.getName();
            if (remotingActionName.lastIndexOf(".") >= 0){
                remotingActionName = remotingActionName.substring(remotingActionName.lastIndexOf(".")+1, remotingActionName.length());
            }
        }
        
        if (REMOTING_API_MAP.get(remotingActionName) == null){
            
            List<ExtJsRemotingApiActionBean> apiBeans = new ArrayList<ExtJsRemotingApiActionBean>();

            Map<String, List<ExtJsRemotingApiActionBean>> map = new HashMap<String, List<ExtJsRemotingApiActionBean>>();
            
            Method[] methods = clazz.getMethods();

            if (methods != null && methods.length > 0){
                for (Method method : methods){
                    if (method.isAnnotationPresent(ExtJsRemotingMethod.class)){
                        int paramLength = calculateParamLength(method.getParameterTypes());
                        boolean isForm = false;
                        if (method.isAnnotationPresent(RequestMapping.class)){
                            RequestMethod[] requestMethods = method.getAnnotation(RequestMapping.class).method();
                            if (requestMethods != null && requestMethods.length > 0){
                                isForm = requestMethods[0].equals(RequestMethod.POST);
                            }
                        }
                        ExtJsRemotingApiActionBean apiBean = new ExtJsRemotingApiActionBean (method.getName(), paramLength, isForm);
                        apiBeans.add(apiBean);
                    }
                }
            }

            
            map.put(remotingActionName, apiBeans);
            REMOTING_API_MAP.put(remotingActionName, createExtRemotingApiString (remotingActionName, remoteServiceUrl, map));
            log.info ("Remoting API added to cache: " + remotingActionName);
        }
        return REMOTING_API_MAP.get(remotingActionName);
    }
    
    private static String createExtRemotingApiString (String apiName, String url, Map<String, List<ExtJsRemotingApiActionBean>> remotingActionMap){
        ExtJsRemotingApiBean remotingActionBean = new ExtJsRemotingApiBean (url, EXT_DIRECT_REMOTING_TYPE, remotingActionMap);
        String output = apiName + " = " + JsonUtil.serializeObjectToJson(remotingActionBean);
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
