package com.ironhack.midterm.model;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.users.AccountHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private boolean penalty;
    private String secretKey;
    @Enumerated(value = EnumType.STRING)
    private Status status;


    public Saving() {
    }

    public Saving(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner,
                  BigDecimal penaltyFee, Status status, BigDecimal minimumBalance, BigDecimal interestRate) {

        super(balance, primaryOwner, secondaryOwner, penaltyFee);

        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        this.referenceDate = LocalDateTime.now();
        this.penalty = false;
        this.status = status;
        this.secretKey = secretKey;
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

    public boolean isPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
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

    public void check() {

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







