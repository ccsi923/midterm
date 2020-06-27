package com.ironhack.midterm.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccessAccountRequest {

    @NotNull
    private Integer id;
    @NotNull( message = "Can be : CreditCard, Student, Checking and Saving")
    private String accountType;

    public AccessAccountRequest(@NotNull Integer id, @NotNull(message = "Can be : CreditCard, Student, Checking and Saving") String accountType) {
        this.id = id;
        this.accountType = accountType;
    }

}
