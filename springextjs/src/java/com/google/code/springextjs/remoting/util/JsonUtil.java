/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author mansari
 */
public class JsonUtil {

    private static final Log log = LogFactory.getLog(JsonUtil.class);
    
    /** Creates a new instance of JSONLibUtil */
    public JsonUtil() {
    }
    
    public static String serializeObjectToJson(Object obj) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        }
        catch (Exception e){
            log.info("Error: " + e, e);
            return null;
        }
        
    }

    public static List serializeJsonArrayToList (String jsonArray,TypeReference typeReference) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Object> result = mapper.readValue(jsonArray, typeReference);
            return result;
        }
        catch (Exception e){
            log.info("Error: " + e, e);
            return null;
        }
    }

    public static Object deserializeJsonToObject (String json, Class clazz){
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        }
        catch (Exception e){
            log.info("Error: " + e, e);
            return null;
        }
    }
}
