package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.AccountUserControllerRepo;
import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.TransactionAccountUserRequest;
import com.ironhack.midterm.model.Transaction;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.service.AccountUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Account User Controller")
@RestController
@RequestMapping("/")
public class AccountUserControllerImpl implements AccountUserControllerRepo {

    @Autowired
    private AccountUserService accountUserService;

    @GetMapping("/account/user/balance")
    @ApiOperation(value = "Find all balance by accountuser",
            notes = "List of all balances by accountuser",
            response = BalanceVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<BalanceVM> findAllBalance(@AuthenticationPrincipal User user){
        return accountUserService.findAllBalance(user);
    }

    @GetMapping("/account/user/balance/{id}")
    @ApiOperation(value = "Find balance by accountuser and id of account",
            notes = "Balance by accountuser giving its account id",
            response = BalanceVM.class)
    @ResponseStatus(HttpStatus.OK)
    public BalanceVM findBalanceByAcountId(@AuthenticationPrincipal User user, @PathVariable("id") Integer accountId){
        return accountUserService.findBalanceByUserAndId(user, accountId);
    }
    @PostMapping("/transaction")
    @ApiOperation(value = "Make a transaction",
            notes = "Make a transaction",
            response = Transaction.class)
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction transactions(@AuthenticationPrincipal User user,
                                    @RequestBody @Valid TransactionAccountUserRequest transactionRequest){
        return accountUserService.transactions( user , transactionRequest);
    }
}
