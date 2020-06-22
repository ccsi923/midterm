package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SavingMV {

    private Integer id;
    private Money balance;
    private AccountHolder primaryOwner;
    private AccountHolder secondaryOwner = null;
    private BigDecimal minimumBalance;
    private BigDecimal penaltyBalance;
    private Status status;
    private BigDecimal interestRate;


}
