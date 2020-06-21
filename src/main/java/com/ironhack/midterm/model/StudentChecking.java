package com.ironhack.midterm.model;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;


@Entity
public class StudentChecking extends Account{

    public StudentChecking() {
    }

    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status) {
        super(balance, secretKey, primaryOwner, secondaryOwner, penaltyFee, status);
    }

}
