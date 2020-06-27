package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.AccountHolderController;

import com.ironhack.midterm.dto.AccountHolderVM;
import com.ironhack.midterm.service.AccountHolderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "AccountHolder Controller")
@RestController
@RequestMapping("/")
public class AccountHolderControllerImpl implements AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;

    @GetMapping("/accountholders")
    @ApiOperation(value = "Find all accounthoolder",
    notes = "List of all accountholders",
    response = AccountHolderVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<AccountHolderVM> findAll(){
        return accountHolderService.findAll();
    }

}
