package com.ironhack.midterm.model.users;

import javax.persistence.Entity;

@Entity
public class AccountUser extends User{

    public AccountUser() {
    }

    public AccountUser(String username, String password) {
        super(username, password);
    }
}
