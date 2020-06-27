package com.ironhack.midterm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.model.Account;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.Data;

import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
@Data
public class CreditCardVM {

    private Integer id;
    private Money balance;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner;
    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private BigDecimal penaltyFee;

    public CreditCardVM(){}

    public CreditCardVM(Integer id, Money balance, AccountHolder primaryOwner,
                        AccountHolder secondaryOwner, BigDecimal creditLimit,
                        BigDecimal interestRate, BigDecimal penaltyFee) {
        this.id = id;
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.penaltyFee = penaltyFee;
    }

}
