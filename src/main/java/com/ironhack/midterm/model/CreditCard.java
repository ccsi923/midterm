package com.ironhack.midterm.model;

import com.ironhack.midterm.model.users.AccountHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table
public class CreditCard extends Account {

    private static final Logger LOGGER = LogManager.getLogger(CreditCard.class);

    private BigDecimal creditLimit;
    private BigDecimal interestRate;

    private LocalDateTime updateDate;


    public CreditCard(){}

    public CreditCard(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner,
                      BigDecimal penaltyFee, BigDecimal creditLimit, BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner, penaltyFee);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.updateDate = LocalDateTime.now();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public void check(){

        int months =  (int) updateDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);

        if(months > 0){
            BigDecimal addValue = balance.getAmount()
                    .multiply(interestRate
                            .divide(new BigDecimal("12"))
                            .add(new BigDecimal("1"))
                            .pow(months));

            setBalance(new Money (addValue));
            updateDate = updateDate.plusYears(Math.floorDiv(months, 12));
            updateDate = updateDate.plusMonths(months % 12);
            LOGGER.debug("[INFO] - Annual interest added: " + addValue);
            LOGGER.info("[INFO] - Annual interest added: " + addValue);
        }
    }

}
