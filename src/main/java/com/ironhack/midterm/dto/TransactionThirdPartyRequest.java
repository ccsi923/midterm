package com.ironhack.midterm.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
@Data
public class TransactionThirdPartyRequest {


    @NotNull
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