package com.ironhack.midterm.model;


import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.service.CreditCardService;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
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
    private LocalDateTime updateDate;

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
        this.updateDate = LocalDateTime.now();
    }

    public void check(){

        int months =  (int) updateDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);

        if(months >= 0){
            BigDecimal decreseValue = balance.decreaseAmount(monthlyMaintenanceFee
                    .multiply(new BigDecimal(months)));

            updateDate = updateDate.plusYears(Math.floorDiv(months, 12));
            updateDate = updateDate.plusMonths(months % 12);
            LOGGER.info("[INFO] - : Monthly Maintenance Fee reduced: " + decreseValue);
        }
    }

}
