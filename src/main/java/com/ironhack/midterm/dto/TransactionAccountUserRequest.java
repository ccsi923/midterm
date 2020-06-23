package com.ironhack.midterm.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionAccountUserRequest {

    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer ownAccountId;
    @NotNull
    private String typeOriginAccount;
    @NotNull
    private Integer foreignAccountId;
    @NotNull
    private String typeDestineAccount;

    @NotNull(message = "Give one name of the receiver (primary o secondary owner)")
    private String receptorName;

    public TransactionAccountUserRequest(){}

    public TransactionAccountUserRequest(@NotNull BigDecimal amount, @NotNull Integer ownAccountId, @NotNull String typeOriginAccount, @NotNull Integer foreignAccountId, @NotNull String typeDestineAccount, @NotNull(message = "Give one name of the receiver (primary o secondary owner)") String receptorName) {
        this.amount = amount;
        this.ownAccountId = ownAccountId;
        this.typeOriginAccount = typeOriginAccount;
        this.foreignAccountId = foreignAccountId;
        this.typeDestineAccount = typeDestineAccount;

        this.receptorName = receptorName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getOwnAccountId() {
        return ownAccountId;
    }

    public void setOwnAccountId(Integer ownAccountId) {
        this.ownAccountId = ownAccountId;
    }

    public Integer getForeignAccountId() {
        return foreignAccountId;
    }

    public void setForeignAccountId(Integer foreignAccountId) {
        this.foreignAccountId = foreignAccountId;
    }


    public String getReceptorName() {
        return receptorName;
    }

    public void setReceptorName(String receptorName) {
        this.receptorName = receptorName;
    }

    public String getTypeOriginAccount() {
        return typeOriginAccount;
    }

    public void setTypeOriginAccount(String typeOriginAccount) {
        this.typeOriginAccount = typeOriginAccount;
    }

    public String getTypeDestineAccount() {
        return typeDestineAccount;
    }

    public void setTypeDestineAccount(String typeDestineAccount) {
        this.typeDestineAccount = typeDestineAccount;
    }
}
