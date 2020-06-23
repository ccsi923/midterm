package com.ironhack.midterm.model;


import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.service.CreditCardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table
public class Checking extends Account {

    private static final Logger LOGGER = LogManager.getLogger(Checking.class);


    private BigDecimal minimumBalance;
    private BigDecimal monthlyMaintenanceFee;
    private String secretKey;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    private boolean penalty;

    public Checking(){}

    public Checking(Money balance, String secretKey, AccountHolder primaryOwner,
                    AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status,
                    BigDecimal minimumBalance, BigDecimal monthlyMaintenanceFee) {

        super(balance, primaryOwner, secondaryOwner, penaltyFee);
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.status = status;
        this.penalty = false;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }

}
