package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Address;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
@Data
public class AccountRequest {

    @NotNull
    private BigDecimal amount;
    @Valid
    private String secretKey;

    @Valid
    private AccountHolder primaryOwner;
    @Valid
    private AccountHolder secondaryOwner;
    @Valid
    private BigDecimal minimumBalance;
    @Valid
    private BigDecimal monthlyMaintenanceFee;
    @Valid
    private BigDecimal interestRate;
    @Valid
    private BigDecimal creditLimit;

    public AccountRequest(){}


}
