package com.ironhack.midterm.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionRequest {

    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer accountId;
    @NotNull
    private String accountType;

    public TransactionRequest() {
    }

    public TransactionRequest(@NotNull BigDecimal amount, @NotNull Integer accountId, @NotNull String accountType) {
        this.amount = amount;
        this.accountId = accountId;
        this.accountType = accountType;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "amount=" + amount +
                ", accountId=" + accountId +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}
