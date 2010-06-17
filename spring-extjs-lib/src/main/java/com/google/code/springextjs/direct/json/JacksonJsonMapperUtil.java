package com.google.code.springextjs.direct.json;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:29:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class JacksonJsonMapperUtil {

    private static final Log log = LogFactory.getLog(JacksonJsonMapperUtil.class);
    
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

    public static List serializeJsonArrayToList (String jsonArray, TypeReference typeReference) {
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
