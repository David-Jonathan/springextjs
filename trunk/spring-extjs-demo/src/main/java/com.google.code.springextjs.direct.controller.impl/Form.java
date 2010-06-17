package com.google.code.springextjs.direct.controller.impl;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: mansari
 * Date: Jun 8, 2010
 * Time: 9:25:34 PM
 * To change this template use File | Settings | File Templates.
 */

public class Form {

    @org.hibernate.validator.constraints.NotEmpty
    @Pattern(regexp = "[a-zA-Z0-9 ]*")
    String name;

    @org.hibernate.validator.constraints.NotEmpty
    @Email
    String email;

    @org.hibernate.validator.constraints.NotEmpty
    @Pattern(regexp = "[a-zA-Z0-9 ]*")
    String company;

    Form() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
