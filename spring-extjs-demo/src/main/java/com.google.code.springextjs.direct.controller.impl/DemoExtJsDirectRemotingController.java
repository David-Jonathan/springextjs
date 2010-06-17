package com.google.code.springextjs.direct.controller.impl;

import com.google.code.springextjs.direct.annotations.ExtJsDirectRemotingAnnotations;
import com.google.code.springextjs.direct.controller.ExtJsDirectRemotingController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author mansari
 */
@Controller
@RequestMapping(value = "/extjs/remoting")
public class DemoExtJsDirectRemotingController extends ExtJsDirectRemotingController implements MessageSourceAware {

    private MessageSource messageSource;

    public DemoExtJsDirectRemotingController() {
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod
    public Long multiply (String num){
        return Long.valueOf(num)*8;
    }

    @ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod
    public String doEcho (String message){
        return message;
    }


    @ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod
    public List<ExtJsTreeNode> getTree (String node){//bug in ExtJs 3.2.1 and prior requires treeload method to accept
                                                    // at least one parameter or UI node which was expanded keeps
                                                    //spinning with loading animation

        List<ExtJsTreeNode> list = new ArrayList<ExtJsTreeNode>();
        list.add(new ExtJsTreeNode("n1", "Node 1", true));
        list.add(new ExtJsTreeNode("n2", "Node 2", true));
        list.add(new ExtJsTreeNode("n3", "Node 3", true));
        return list;
    }

    @ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod (formLoad = true)
    public Form loadForm (String name, String email){
        Form form = new Form();
        form.setName(name);
        form.setEmail(email);
        return form;
    }


    @ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod
    @RequestMapping (value="/updateForm", method = RequestMethod.POST)
    public ModelAndView updateForm(Locale locale, HttpServletRequest request, @Valid Form form, BindingResult result){
        String message = null;
        if (!result.hasErrors()){
            message = this.messageSource.getMessage("success.message", null, locale);
        }
        ModelAndView mnv = new ModelAndView();
        mnv.addObject("message", message);
        return mnv;
    }

}

class ExtJsTreeNode {

    private String id;
    private String text;
    private boolean leaf;


    public ExtJsTreeNode(String id, String text, boolean leaf) {
        this.text = text;
        this.leaf = leaf;
        this.id = id;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}