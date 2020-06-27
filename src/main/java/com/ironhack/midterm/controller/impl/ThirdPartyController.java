package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.StudentCheckingVM;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.TransactionThirdPartyRequest;
import com.ironhack.midterm.model.Transaction;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.service.ThirdPartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Thirdparty Controller")
@RestController
@RequestMapping("/")
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;


    /**  CREDIT  **/

    @PostMapping("/debit/thirdparty")
    @ApiOperation(value = "Thirdparty debit an account",
            notes = "Debit an account being thirparty")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@RequestBody @Valid TransactionThirdPartyRequest transactionRequest){
        thirdPartyService.debit(transactionRequest);
    }

    /**  DEBIT  **/

    @PostMapping("/credit/thirdparty")
    @ApiOperation(value = "Thirdparty credit an account",
            notes = "Credit an account being thirparty")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@RequestBody @Valid TransactionThirdPartyRequest transactionRequest){
        thirdPartyService.credit(transactionRequest);
    }

}
