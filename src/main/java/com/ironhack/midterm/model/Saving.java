package com.ironhack.midterm.model;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.users.AccountHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
@Table
public class Saving extends Account{

    private static final Logger LOGGER = LogManager.getLogger(Saving.class);


    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private LocalDateTime referenceDate;

    public Saving() {
    }

    public Saving(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner,
                  BigDecimal penaltyFee, Status status, BigDecimal minimumBalance, BigDecimal interestRate) {

        super(balance, secretKey, primaryOwner, secondaryOwner, penaltyFee, status);

        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        this.referenceDate = LocalDateTime.now();
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

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDateTime referenceDate) {
        this.referenceDate = referenceDate;
    }


    public void check(Saving saving) {

        if (balance.getAmount().compareTo(minimumBalance) < 0) {
            balance.decreaseAmount(getPenaltyFee());
            LOGGER.info("[INFO] - amount less than 250. Penalty reduced");
        }

        int years = (int) referenceDate.until(LocalDateTime.now(), ChronoUnit.YEARS);

        if ( years >= 0) {

            BigDecimal addValue = balance.getAmount()
                        .multiply(getInterestRate()
                        .add(new BigDecimal("1"))
                        .pow(years));

                setBalance(new Money(addValue));
            referenceDate = referenceDate.plusYears(years);
            LOGGER.info("[INFO] - Annual interest added: " + addValue);

            }
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







