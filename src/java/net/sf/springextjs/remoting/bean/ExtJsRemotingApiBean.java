/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.springextjs.remoting.bean;


import java.util.List;
import java.util.Map;

/**
 *
 * @author mansari
 */
public class ExtJsRemotingApiBean {
    private String url;
    private final String type;
    private Map<String, List<ExtJsRemotingApiActionBean>> actions;

    public ExtJsRemotingApiBean(String url, String type, Map<String, List<ExtJsRemotingApiActionBean>> actions) {
        this.actions = actions;//new ArrayList<ExtRemotingApiActionBean>();
        this.url = url;
        this.type = type;
    }

    public Map<String, List<ExtJsRemotingApiActionBean>> getActions() {
        return actions;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

}
