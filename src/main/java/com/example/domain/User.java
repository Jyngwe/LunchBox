package com.example.domain;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;

public class User {

    @Size(min = 1)
    private String userName;
    private String password;
    @Email
    private String mail;

    public User() {

    }

    public User(String userName, String password, String mail) {
        this.userName = userName;
        this.password = password;
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
