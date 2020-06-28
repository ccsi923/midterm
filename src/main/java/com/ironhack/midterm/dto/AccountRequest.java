package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Address;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.http.HttpResponse;

@Data
public class AccountRequest {

    @NotNull
    @Digits(integer=12, fraction=2, message = "Maximum 12 integer digits and 2 fraction digits")
    @DecimalMin(value = "0", inclusive = false, message = "Amount to create an account must be grater than 0")
    private BigDecimal amount;
    @Valid
    private String secretKey;

    @Valid
    private AccountHolder primaryOwner;
    @Valid
    private AccountHolder secondaryOwner;
    @Valid
    @Digits(integer=3, fraction=2, message = "Maximum 3 integer digits and 2 fraction digits")
    @DecimalMin(value = "0", inclusive = false, message = "MinimumBalance must be grater than 0")
    private BigDecimal minimumBalance;

    @Valid
    @Digits(integer=0, fraction=5, message = "Maximum 0 integer digits and 5 fraction digits")
    @DecimalMin(value = "0", inclusive = false, message = "InterestRate can't be negative")
    private BigDecimal interestRate;
    @Valid
    @DecimalMax(value = "100000",inclusive = true, message = "Maximum amount 100000")
    @DecimalMin(value = "100", inclusive = true, message = "Minimum amount 100")
    @DecimalMin(value = "0", inclusive = false, message = "Amount to create an account must be grater than 0")
    private BigDecimal creditLimit;

    public AccountRequest(){}


}
