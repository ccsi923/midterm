package com.ironhack.midterm.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionThirdPartyRequest {


    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer accountId;
    @NotNull(message = "Can be : checking, student and saving account")
    private String accountType;
    @NotNull
    private String secretKey;

    public TransactionThirdPartyRequest() {
    }

    public TransactionThirdPartyRequest(@NotNull BigDecimal amount, @NotNull Integer accountId, @NotNull String accountType, @NotNull String secretKey) {
        this.amount = amount;
        this.accountId = accountId;
        this.accountType = accountType;
        this.secretKey = secretKey;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
