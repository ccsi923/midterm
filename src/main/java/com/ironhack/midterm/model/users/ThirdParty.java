package com.ironhack.midterm.model.users;

import javax.persistence.Entity;

@Entity
public class ThirdParty extends User {

    private String hashkey;

    public ThirdParty() {
    }

    public ThirdParty(String username, String password) {
        super(username, password);
    }

    public ThirdParty(String username, String password, String hashkey) {
        super(username, password);
        this.hashkey = hashkey;
    }

    public String getHashkey() {
        return hashkey;
    }

    public void setHashkey(String hashkey) {
        this.hashkey = hashkey;
    }
}
