/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.springextjs.remoting.spring3.view;


import java.util.Map;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 *
 * @author mansari
 */
public class ExtJsRemotingJacksonJsonView extends MappingJacksonJsonView{
     
    public ExtJsRemotingJacksonJsonView() {
        super();
    }
    
    /**
     * Filters out undesired attributes from the given model. The return value can be either another {@link Map}, or a
     * single value object.
     *
     * <p>Default implementation removes {@link BindingResult} instances and entries not included in the {@link
     * #setRenderedAttributes(Set) renderedAttributes} property.
     *
     * @param model the model, as passed on to {@link #renderMergedOutputModel}
     * @return the object to be rendered
     */
    protected Object filterModel(Map<String, Object> model) {
        for (String key : model.keySet()){
            if (key.equals("JSONObject") || key.equals("JSONObjectList")
                || key.equals("extJsDirectRemotingResponseBean") || key.equals("extJsDirectRemotingResponseBeanList"))
                return model.get(key);
        }
        return null;
    }
}
