package com.ironhack.midterm.model;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.users.AccountHolder;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table
public class Saving extends Account{


    private BigDecimal minimumBalance;
    private BigDecimal interestRate;

    public Saving() {
    }

    public Saving(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status, BigDecimal minimumBalance, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner, secondaryOwner, penaltyFee, status);
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "Saving{" +
                "minimumBalance=" + minimumBalance +
                ", interestRate=" + interestRate +
                ", id=" + id +
                ", balance=" + balance +
                ", secretKey='" + secretKey + '\'' +
                ", primaryOwner=" + primaryOwner +
                ", secondaryOwner=" + secondaryOwner +
                ", penaltyFee=" + penaltyFee +
                ", status=" + status +
                '}';
    }
}
