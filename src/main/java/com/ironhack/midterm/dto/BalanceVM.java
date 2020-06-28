package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceVM {

    private Integer id;
    private String name;
    private BigDecimal balance;

}
