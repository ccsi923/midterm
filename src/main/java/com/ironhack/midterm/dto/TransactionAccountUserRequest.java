package com.ironhack.midterm.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransactionAccountUserRequest {

    @NotNull
    @Digits(integer=12, fraction=2, message = "Maximum 12 integer digits and 2 fraction digits")
    @DecimalMin(value = "0", inclusive = false, message = "Amount must be grater than 0")
    private BigDecimal amount;
    @NotNull
    private Integer ownAccountId;
    @NotNull(message = "Can be: Student, CreditCard, Saving and Checking")
    private String typeOriginAccount;
    @NotNull
    private Integer foreignAccountId;
    @NotNull(message = "Can be: Student, CreditCard, Saving and Checking")
    private String typeDestineAccount;

    @NotNull(message = "Give one name of the receiver (primary o secondary owner)")
    private String receptorName;

    public TransactionAccountUserRequest() {
    }


    public TransactionAccountUserRequest(@NotNull BigDecimal amount,
                                         @NotNull Integer ownAccountId,
                                         @NotNull String typeOriginAccount,
                                         @NotNull Integer foreignAccountId,
                                         @NotNull String typeDestineAccount,
                                         @NotNull(message = "Give one name of the receiver (primary o secondary owner)") String receptorName) {
        this.amount = amount;
        this.ownAccountId = ownAccountId;
        this.typeOriginAccount = typeOriginAccount;
        this.foreignAccountId = foreignAccountId;
        this.typeDestineAccount = typeDestineAccount;
        this.receptorName = receptorName;
    }

}
