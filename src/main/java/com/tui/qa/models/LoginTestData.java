package com.tui.qa.models;

public class LoginTestData {

    private LoginData validUser;
    private LoginData invalidUser;

    public LoginData getValidUser() {
        return validUser;
    }

    public void setValidUser(LoginData validUser) {
        this.validUser = validUser;
    }

    public LoginData getInvalidUser() {
        return invalidUser;
    }

    public void setInvalidUser(LoginData invalidUser) {
        this.invalidUser = invalidUser;
    }
}
