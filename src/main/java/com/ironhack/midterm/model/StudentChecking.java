package com.ironhack.midterm.model;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
@Entity
public class StudentChecking extends Account{

    private String secretKey;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    public StudentChecking() {
    }
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status) {
        super(balance, primaryOwner, secondaryOwner, penaltyFee);
        this.status = status;
        this.secretKey = secretKey;
    }
}
