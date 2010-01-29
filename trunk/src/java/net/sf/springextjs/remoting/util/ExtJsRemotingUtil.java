/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.springextjs.remoting.util;



import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.springextjs.remoting.annotations.Annotations.ExtJsRemotingMethod;
import net.sf.springextjs.remoting.bean.ExtJsDirectRemotingRequestBean;
import net.sf.springextjs.remoting.bean.ExtJsRemotingApiActionBean;
import net.sf.springextjs.remoting.bean.ExtJsRemotingApiBean;
import net.sf.springextjs.remoting.spring3.ExtJsRemotingController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author mansari
 */
public class ExtJsRemotingUtil {

    private static final Log log = LogFactory.getLog(ExtJsRemotingUtil.class);

    private static final Map<String,String> REMOTING_API_MAP = new HashMap<String,String>();

    public static final String EXT_DIRECT_REMOTING_TYPE = "remoting";
    public static final String EXT_DIRECT_POLLING_TYPE = "polling";

    public static String createExtRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz){
        return createExtRemotingApiStringFromClass (remoteServiceUrl, clazz, null);
    }

    public static String createExtRemotingApiString (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz, String remotingActionName){
        return createExtRemotingApiStringFromClass (remoteServiceUrl, clazz, remotingActionName);
    }

    private static String createExtRemotingApiStringFromClass (String remoteServiceUrl, Class<? extends ExtJsRemotingController> clazz, String remotingActionName){
        //clazz.
        if (REMOTING_API_MAP.get(clazz.toString()) == null){
            
            List<ExtJsRemotingApiActionBean> apiBeans = new ArrayList<ExtJsRemotingApiActionBean>();

            Map<String, List<ExtJsRemotingApiActionBean>> map = new HashMap<String, List<ExtJsRemotingApiActionBean>>();
            
            Method[] methods = clazz.getMethods();

            if (methods != null && methods.length > 0){
                for (Method method : methods){
                    if (method.isAnnotationPresent(ExtJsRemotingMethod.class)){
                        int paramLength = method.getAnnotation(ExtJsRemotingMethod.class).paramLength();
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

            //use class name if the remotingActionName is null
            if (remotingActionName == null){
                remotingActionName = clazz.toString();
                if (remotingActionName.lastIndexOf(".") >= 0){
                    remotingActionName = remotingActionName.substring(remotingActionName.lastIndexOf(".")+1, remotingActionName.length());
                }
            }
            map.put(remotingActionName, apiBeans);
            REMOTING_API_MAP.put(clazz.toString(), createExtRemotingApiString ("REMOTING_API", remoteServiceUrl, map));
        }
        return REMOTING_API_MAP.get(clazz.toString());
    }
    
    private static String createExtRemotingApiString (String apiName, String url, Map<String, List<ExtJsRemotingApiActionBean>> remotingActionMap){
        ExtJsRemotingApiBean remotingActionBean = new ExtJsRemotingApiBean (url, EXT_DIRECT_REMOTING_TYPE, remotingActionMap);
        String output = apiName + " = " + JsonLibUtil.serializeObjectToJSONObject(remotingActionBean).toString();
        return output;
    }

    /**
     * This method abstracts the http requet by transforming the raw
     * http request to one or more ExtJsDirectRemotingRequestBean's.
     * Returns list of ExtJs remoting requests.
     * @param request Http request
     * @return List of one or more ExtJsDirectRemotingRequestBean objects.
     *  In case of form submit using post, the list will contain only one object.
     */
    public static List<ExtJsDirectRemotingRequestBean> getExtDirectRemotingRequestBeans (HttpServletRequest request){
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
                    JSONArray jarray = JsonLibUtil.serializeObjectToJSONArray(rawRequestString);
                    for (int i = 0; i < jarray.size(); i++){
                        ExtJsDirectRemotingRequestBean extReqBean =
                            (ExtJsDirectRemotingRequestBean) JsonLibUtil.deserializeJSONObjectToObject(jarray.getJSONObject(i),ExtJsDirectRemotingRequestBean.class);
                        reqs.add(extReqBean);
                    }
                }
                //only one extjs remoting sent with this http request
                else{
                    JSONObject jsonObject = JsonLibUtil.serializeObjectToJSONObject(rawRequestString);
                    ExtJsDirectRemotingRequestBean extReqBean =
                            (ExtJsDirectRemotingRequestBean) JsonLibUtil.deserializeJSONObjectToObject(jsonObject,ExtJsDirectRemotingRequestBean.class);
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
        String requestString = IOUtils.toString(request.getReader());
        return requestString;
    }

}
