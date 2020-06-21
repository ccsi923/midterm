package com.ironhack.midterm.dto;

import com.ironhack.midterm.model.users.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class AccountHolderMV {

    private Integer id;
    private String name;
    private LocalDate birth;
    private Address primaryAddress;
    private Address secondaryAddress;
    private boolean logged;


}
