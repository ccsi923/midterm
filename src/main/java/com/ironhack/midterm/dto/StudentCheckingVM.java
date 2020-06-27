package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Account;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class StudentCheckingVM {

    private Integer id;
    private Money balance;
    private String secretKey;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner;
    private BigDecimal penaltyFee;
    private Status status;

    public StudentCheckingVM(Integer id, Money balance, String secretKey, AccountHolder primary,
                             AccountHolder secondary, BigDecimal penaltyFee, Status status) {
        this.id = id;
        this.balance = balance;
        this.secretKey = secretKey;
        this.primaryOwner = primary;
        this.secondaryOwner = secondary;
        this.penaltyFee = penaltyFee;
        this.status = status;
    }

}


