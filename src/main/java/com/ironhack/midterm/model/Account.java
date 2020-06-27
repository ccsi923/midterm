package com.ironhack.midterm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected Integer id;

    @Embedded
    protected Money balance;


    @ManyToOne
    @JsonIgnore
    protected AccountHolder primaryOwner;

    @ManyToOne
    @JsonIgnore
    protected AccountHolder secondaryOwner;

    protected BigDecimal penaltyFee;



    public Account(){}

    public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal penaltyFee) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.penaltyFee = penaltyFee;

    }

}
