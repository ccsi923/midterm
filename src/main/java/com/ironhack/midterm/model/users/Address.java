package com.ironhack.midterm.model.users;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class Address {


    @NotNull
    private String city;

    @NotNull
    private String country;

    @NotNull
    private String localAddress;

    @NotNull
    private String postalCode;

    public Address() {
    }

    public Address(@NotNull String city, @NotNull String country, @NotNull String localAddress, @NotNull String postalCode) {
        this.city = city;
        this.country = country;
        this.localAddress = localAddress;
        this.postalCode = postalCode;
    }
}
