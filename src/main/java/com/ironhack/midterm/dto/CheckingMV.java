package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;

import java.math.BigDecimal;

public class CheckingMV {

    private Integer id;
    private Money balance;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner;
    private BigDecimal penaltyFee;
    private Status status;
    private BigDecimal minimumBalance;
    private BigDecimal monthlyMaintenanceFee;

    public CheckingMV(Integer id, Money balance, AccountHolder primaryOwner,
                      AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status,
                      BigDecimal minimumBalance, BigDecimal monthlyMaintenanceFee) {
        this.id = id;
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.penaltyFee = penaltyFee;
        this.status = status;
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
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

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }
}
