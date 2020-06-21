package com.ironhack.midterm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;

import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

public class CreditCardMV {

    private Integer id;
    private Money balance;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner;
    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private BigDecimal penaltyFee;

    public CreditCardMV(Integer id, Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal creditLimit, BigDecimal interestRate, BigDecimal penaltyFee) {
        this.id = id;
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.penaltyFee = penaltyFee;
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

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }
}
