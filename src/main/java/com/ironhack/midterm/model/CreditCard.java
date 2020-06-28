package com.ironhack.midterm.model;

import com.ironhack.midterm.model.users.AccountHolder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.standard.processor.StandardHrefTagProcessor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table
public class CreditCard extends Account {

    private static final Logger LOGGER = LogManager.getLogger(CreditCard.class);

    private BigDecimal creditLimit;
    @Column(precision = 5, scale = 4)
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

    public void check(){

        if(balance.getAmount().compareTo(new BigDecimal("0")) < 0){
            LOGGER.info("It cannot update because balance is negative");
            return;
        } else {

            int months = (int) updateDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);

            if (months >= 0) {
                BigDecimal addValue = balance.getAmount()
                        .multiply(interestRate
                                .divide(new BigDecimal("12"),16, RoundingMode.HALF_EVEN)
                                .add(new BigDecimal("1"))
                                .pow(months));

                setBalance(new Money(addValue));
                updateDate = updateDate.plusYears(Math.floorDiv(months, 12));
                updateDate = updateDate.plusMonths(months % 12);
                LOGGER.info("[INFO] - Annual interest added: " + addValue);
            }
        }
    }

}
