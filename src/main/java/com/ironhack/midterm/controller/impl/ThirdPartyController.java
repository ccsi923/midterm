package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.TransactionThirdPartyRequest;
import com.ironhack.midterm.model.Transaction;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.service.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;


    /**  CREDIT  **/

    @PostMapping("/debit/thirdparty")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@RequestBody @Valid TransactionThirdPartyRequest transactionRequest, @AuthenticationPrincipal ThirdParty thirdParty){
        thirdPartyService.debit(transactionRequest, thirdParty);
    }

    /**  DEBIT  **/

    @PostMapping("/credit/thirdparty/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@RequestBody @Valid TransactionThirdPartyRequest transactionRequest, @AuthenticationPrincipal ThirdParty thirdParty){
        thirdPartyService.credit(transactionRequest, thirdParty);
    }
}
