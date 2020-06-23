package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class UserRequest {

    @NotNull
    private String userName;
    @NotNull
    private String password;


    private UserRequest(){}

    public UserRequest(@NotNull String userName, @NotNull String password) {
        this.userName = userName;
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
