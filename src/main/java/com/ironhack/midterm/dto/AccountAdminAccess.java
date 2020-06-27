package com.ironhack.midterm.dto;

import com.ironhack.midterm.model.Money;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountAdminAccess {

    private Integer id;
    private String name;
    private Money balance;

    public AccountAdminAccess(Integer id, String name, Money balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

}
