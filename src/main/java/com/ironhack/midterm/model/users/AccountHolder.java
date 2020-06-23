package com.ironhack.midterm.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.model.Account;
import com.ironhack.midterm.model.Saving;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private LocalDate birth;
    @NotNull
    private String password;
    //@OneToOne
    //private AccountUser accountUser;


    @Valid
    @NotNull
    @Embedded
    private Address primaryAddress;

    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "mailingCity")),
            @AttributeOverride(name = "country", column = @Column(name = "mailingCountry")),
            @AttributeOverride(name = "localAddress", column = @Column(name = "mailingLocalAddress")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "mailingPostalCode"))
    })
    private Address mailingAddress;

    private boolean login;

    @OneToMany(mappedBy = "primaryOwner", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Account> primaryAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "secondaryOwner", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Account> secondaryAccounts = new ArrayList<>();

    public AccountHolder(){}

    public AccountHolder(@NotNull String name, @NotNull LocalDate birth,
                         @NotNull Address primaryAddress, @NotNull String password
                         ) {
        this.name = name;
        this.birth = birth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = null;
        this.login = false;
        this.password = password;

    }

    public AccountHolder(@NotNull String name, @NotNull LocalDate birth, @NotNull String password,
                         @NotNull Address primaryAddress, Address mailingAddress) {
        this.name = name;
        this.birth = birth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
        this.login = false;
        this.password = password;

    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogged(){
        return login;
    }

    public void setLogin(boolean logged){
        this.login = logged;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public List<Account> getPrimaryAccounts() {
        return primaryAccounts;
    }

    public void setPrimaryAccounts(List<Account> primaryAccounts) {
        this.primaryAccounts = primaryAccounts;
    }

    public List<Account> getSecondaryAccounts() {
        return secondaryAccounts;
    }

    public void setSecondaryAccounts(List<Account> secondaryAccounts) {
        this.secondaryAccounts = secondaryAccounts;
    }

    //public AccountUser getAccountUser() {
      //  return accountUser;
    //}

    //public void setAccountUser(AccountUser accountUser) {
      //  this.accountUser = accountUser;
    //}

    //public boolean isLogin() {
      //  return login;
    //}
}
