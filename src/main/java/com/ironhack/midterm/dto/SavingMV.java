package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class SavingMV {

    private Integer id;
    private Money balance;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner = null;
    private BigDecimal minimumBalance;
    private BigDecimal penaltyBalance;
    private Status status;
    private BigDecimal interestRate;
    private boolean penalty;

    public SavingMV(Integer id, Money balance, AccountHolder primaryOwner,
                    AccountHolder secondaryOwner, BigDecimal minimumBalance,
                    BigDecimal penaltyBalance, Status status, BigDecimal interestRate, boolean penalty) {
        this.id = id;
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.minimumBalance = minimumBalance;
        this.penaltyBalance = penaltyBalance;
        this.status = status;
        this.interestRate = interestRate;
        this.penalty = penalty;
    }
}
