package com.ironhack.midterm.model.users;

import javax.persistence.Entity;

@Entity
public class ThirdParty extends User {

    public ThirdParty() {
    }

    public ThirdParty(String username, String password) {
        super(username, password);
    }


}
