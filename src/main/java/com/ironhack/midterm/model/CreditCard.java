package com.ironhack.midterm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
@Table
public class CreditCard {

    private static final Logger LOGGER = LogManager.getLogger(CreditCard.class);


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Embedded
    private Money balance;

    @NotNull
    @ManyToOne
    @JsonIgnore
    private AccountHolder primaryOwner;

    @ManyToOne
    @JsonIgnore
    private AccountHolder secondaryOwner;

    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private BigDecimal penaltyFee;
    private LocalDateTime updateDate;


    public CreditCard(){}

    public CreditCard(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal creditLimit, BigDecimal interestRate, BigDecimal penaltyFee) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.penaltyFee = penaltyFee;
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

    public void check(CreditCard creditCard){

        int months =  (int) creditCard.getUpdateDate().until(LocalDateTime.now(), ChronoUnit.MONTHS);

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
