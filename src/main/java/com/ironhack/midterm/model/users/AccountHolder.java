package com.ironhack.midterm.model.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.ironhack.midterm.model.Account;
import com.ironhack.midterm.model.Saving;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
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

    @OneToOne
    private AccountUser accountUser;


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

    @OneToMany(mappedBy = "primaryOwner", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Account> primaryAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "secondaryOwner", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Account> secondaryAccounts = new ArrayList<>();

    public AccountHolder(){}

    public AccountHolder(@NotNull String name, @NotNull LocalDate birth,
                         @NotNull Address primaryAddress
                         ) {
        this.name = name;
        this.birth = birth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = null;

    }
    public AccountHolder(@NotNull String name, @NotNull LocalDate birth,
                         @NotNull Address primaryAddress, Address mailingAddress) {
        this.name = name;
        this.birth = birth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;

    }

}
