package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class UserDTO {

    @NotNull
    private String userName;
    @NotNull
    private String password;
    @Valid
    private String haskey;

    private UserDTO(){}

    public UserDTO(@NotNull String userName, @NotNull String password) {
        this.userName = userName;
        this.password = password;
    }

    public UserDTO(@NotNull String userName, @NotNull String password, @Valid String haskey) {
        this.userName = userName;
        this.password = password;
        this.haskey = haskey;
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

    public String getHaskey() {
        return haskey;
    }

    public void setHaskey(String haskey) {
        this.haskey = haskey;
    }
}
