package com.example.demo.DTO;

public class NameForm {
    private String userName;

    // Getters and Setters are REQUIRED for Spring to work
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}