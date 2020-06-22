package com.ironhack.midterm.model;


import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.service.CreditCardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table
public class Checking extends Account {

    private static final Logger LOGGER = LogManager.getLogger(Checking.class);


    private BigDecimal minimumBalance;
    private BigDecimal monthlyMaintenanceFee;

    public Checking(){}

    public Checking(Money balance, String secretKey, AccountHolder primaryOwner,
                    AccountHolder secondaryOwner, BigDecimal penaltyFee, Status status,
                    BigDecimal minimumBalance, BigDecimal monthlyMaintenanceFee) {

        super(balance, secretKey, primaryOwner, secondaryOwner, penaltyFee, status);
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
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

    public void check(Checking checking){

        if (balance.getAmount().compareTo(minimumBalance) < 0) {
            balance.decreaseAmount(getPenaltyFee());
            LOGGER.info("[INFO] - amount less than "+ minimumBalance +". Penalty reduced");
        }

    }
}
