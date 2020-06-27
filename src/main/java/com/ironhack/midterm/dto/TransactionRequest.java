package com.ironhack.midterm.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionRequest {

    @NotNull
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
