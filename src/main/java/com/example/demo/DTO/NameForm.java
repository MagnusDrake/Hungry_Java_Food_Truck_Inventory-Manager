package com.example.demo.DTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class NameForm {
    @NotBlank(message = "Name is required")
    private String userName;

    // Getters and Setters are REQUIRED for Spring to work
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Valid String userName) {
        this.userName = userName;
    }
}