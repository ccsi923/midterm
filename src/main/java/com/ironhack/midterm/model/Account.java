package com.ironhack.midterm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected Integer id;

    @Embedded
    protected Money balance;
    protected String secretKey;

    @ManyToOne
    @JsonIgnore
    protected AccountHolder primaryOwner;

    @ManyToOne
    @JsonIgnore
    protected AccountHolder secondaryOwner;

    protected BigDecimal penaltyFee;
    @Enumerated(value = EnumType.STRING)
    protected Status status;

    public Account(){}

    public Account(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status) {
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
