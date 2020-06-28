package com.ironhack.midterm.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionRequest {

    @NotNull
    @Digits(integer=12, fraction=2, message = "Maximum 12 integer digits and 2 fraction digits")
    @DecimalMin(value = "0", inclusive = false, message = "Amount must be grater than 0")
    private BigDecimal amount;
    @NotNull
    private Integer accountId;

    public TransactionRequest() {
    }

    public TransactionRequest(@NotNull BigDecimal amount, @NotNull Integer accountId) {
        this.amount = amount;
        this.accountId = accountId;

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

}
