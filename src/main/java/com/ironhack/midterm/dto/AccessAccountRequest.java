package com.ironhack.midterm.dto;

import javax.validation.constraints.NotNull;

public class AccessAccountRequest {

    @NotNull
    private Integer id;
    @NotNull( message = "Can be : CreditCard, Student, Checking and Saving")
    private String accountType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
