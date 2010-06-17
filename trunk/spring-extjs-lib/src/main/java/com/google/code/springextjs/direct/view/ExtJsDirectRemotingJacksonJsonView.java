package com.google.code.springextjs.direct.view;

import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:28:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingJacksonJsonView extends MappingJacksonJsonView {

    public ExtJsDirectRemotingJacksonJsonView() {
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
    @Override
    protected Object filterModel(Map<String, Object> model) {
        return model.get(model.keySet().iterator().next());
    }
}
