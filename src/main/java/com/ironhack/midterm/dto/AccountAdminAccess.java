package com.ironhack.midterm.dto;

import com.ironhack.midterm.model.Money;

import java.math.BigDecimal;

public class AccountAdminAccess {

    private Integer id;
    private String name;
    private Money balance;

    public AccountAdminAccess(Integer id, String name, Money balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }
}
