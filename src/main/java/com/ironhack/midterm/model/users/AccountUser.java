package com.ironhack.midterm.model.users;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class AccountUser extends User{

    //@OneToOne(mappedBy = "accountUser", cascade = CascadeType.ALL)
    //private AccountHolder accountHolder;

    public AccountUser() {
    }

    public AccountUser(String username, String password) {
        super(username, password);

    }

    //public AccountHolder getAccountHolder() {
      //  return accountHolder;
    //}

    //public void setAccountHolder(AccountHolder accountHolder) {
      //  this.accountHolder = accountHolder;
    //}
}
