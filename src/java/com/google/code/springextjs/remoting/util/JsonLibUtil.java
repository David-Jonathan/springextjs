/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 *
 * @author mansari
 */
public class JsonLibUtil {

    /** Creates a new instance of JSONLibUtil */
    public JsonLibUtil() {
    }
    
    public static JSONObject serializeObjectToJSONObject (Object obj) {
        if (obj == null)
            return null;
        else{
            JsonConfig jsonConfig = createJsonConfig ();
            return JSONObject.fromObject(obj, jsonConfig);
        }
    }

    public static JSONArray serializeObjectToJSONArray (Object obj) {
        if (obj == null)
            return null;
        else{
            JsonConfig jsonConfig = createJsonConfig ();
            return JSONArray.fromObject(obj, jsonConfig);
        }
    }

    public static Object deserializeJSONObjectToObject (JSONObject jsonObject, Class clazz){
        JsonConfig jsonConfig = createJsonConfig();
        jsonConfig.setRootClass(clazz);
        return JSONObject.toBean(jsonObject, jsonConfig);
    }

    protected static JsonConfig createJsonConfig (){
         JsonConfig jsonConfig = new JsonConfig ();
         jsonConfig.setIgnoreTransientFields(true);
         return jsonConfig;
    }
}
