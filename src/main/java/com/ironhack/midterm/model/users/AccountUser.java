package com.ironhack.midterm.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class AccountUser extends User{

    @OneToOne(mappedBy = "accountUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private AccountHolder accountHolder;

    public AccountUser() {
    }

    public AccountUser(String username, String password) {
        super(username, password);
    }

    public AccountUser(String username, String password, AccountHolder accountHolder) {
        super(username, password);
        this.accountHolder = accountHolder;

    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }
}
