package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;

import java.math.BigDecimal;

public class StudentCheckingMV {

    private Integer id;
    private Money balance;
    private String secretKey;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner;
    private BigDecimal penaltyFee;
    private Status status;

    public StudentCheckingMV(Integer id, Money balance, String secretKey, AccountHolder primaryOwner,
                             AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status) {
        this.id = id;
        this.balance = balance;
        this.secretKey = secretKey;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.penaltyFee = penaltyFee;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}


