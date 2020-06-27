package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
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


}
