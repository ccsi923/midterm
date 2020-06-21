package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.AccountHolderController;

import com.ironhack.midterm.dto.AccountHolderMV;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.service.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountHolderControllerImpl implements AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;

    @GetMapping("/accountholders")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountHolderMV> findAll(){
        return accountHolderService.findAll();
    }

    @PostMapping("/accountholder")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolderMV create(@RequestBody @Valid AccountHolder accountHolder){
        return accountHolderService.create(accountHolder);
    }
}
