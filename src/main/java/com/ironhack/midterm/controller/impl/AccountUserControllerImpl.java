package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.TransactionAccountUserRequest;
import com.ironhack.midterm.dto.TransactionThirdPartyRequest;
import com.ironhack.midterm.model.Transaction;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountUserControllerImpl {

    @Autowired
    private AccountUserService accountUserService;

    //@GetMapping("/account/user/balance")
    //@ResponseStatus(HttpStatus.OK)
    //public List<BalanceVM> findAllBalance(@AuthenticationPrincipal User user){
    //    return accountUserService.findAllBalance();
    //}

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction transactions(@RequestBody @Valid TransactionAccountUserRequest transactionRequest, @AuthenticationPrincipal User user){
        return accountUserService.transactions(transactionRequest, user);
    }
}
