package com.ironhack.midterm.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
@Data
public class TransactionThirdPartyRequest {


    @NotNull
    @Digits(integer=12, fraction=2, message = "Maximum 12 integer digits and 2 fraction digits")
    @DecimalMin(value = "0", inclusive = false, message = "Amount must be grater than 0")
    private BigDecimal amount;
    @NotNull
    private Integer accountId;
    @NotNull
    private String secretKey;

    public TransactionThirdPartyRequest() {
    }
    public TransactionThirdPartyRequest(@NotNull BigDecimal amount, @NotNull Integer accountId, @NotNull String secretKey) {
        this.amount = amount;
        this.accountId = accountId;
        this.secretKey = secretKey;
    }
}