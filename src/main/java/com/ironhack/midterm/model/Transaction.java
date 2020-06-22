package com.ironhack.midterm.model;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public Transaction() {
    }

    public Transaction(Account accountSender, Account accountReceptor) {
        this.accountSender = accountSender;
        this.accountReceptor = accountReceptor;
        this.dateTransaction = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
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
}
