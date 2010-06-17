package com.google.code.springextjs.direct.api;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Mar 2, 2010
 * Time: 11:26:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtJsDirectRemotingApi {

    private String url;
    private final String type;
    private Map<String, List<ExtJsDirectRemotingApiActionElement>> actions;

    public ExtJsDirectRemotingApi(String url, String type, Map<String, List<ExtJsDirectRemotingApiActionElement>> actions) {
        this.actions = actions;//new ArrayList<ExtRemotingApiActionBean>();
        this.url = url;
        this.type = type;
    }

    public Map<String, List<ExtJsDirectRemotingApiActionElement>> getActions() {
        return actions;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
