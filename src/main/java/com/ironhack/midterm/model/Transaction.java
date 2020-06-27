package com.ironhack.midterm.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime dateTransaction;

    @ManyToOne
    @JoinColumn(name = "senderId")
    private Account accountSender;
    @ManyToOne
    @JoinColumn(name = "receptorId")
    private Account accountReceptor;

    private BigDecimal amount;

    public Transaction() {
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction() {
        this.dateTransaction = LocalDateTime.now();
    }

    public Account getAccountSender() {
        return accountSender;
    }

    public void setAccountSender(Account accountSender) {
        this.accountSender = accountSender;
    }

    public Account getAccountReceptor() {
        return accountReceptor;
    }

    public void setAccountReceptor(Account accountReceptor) {
        this.accountReceptor = accountReceptor;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal ammount) {
        this.amount = ammount;
    }
}
