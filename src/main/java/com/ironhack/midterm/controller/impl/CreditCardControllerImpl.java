package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.CreditCardController;
import com.ironhack.midterm.dto.AccountDto;
import com.ironhack.midterm.dto.CreditCardMV;
import com.ironhack.midterm.dto.SavingMV;
import com.ironhack.midterm.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CreditCardControllerImpl implements CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping("/credicards")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCardMV> findAll(){
        return creditCardService.findAll();
    }

    @PostMapping("/creditcard")
    @ResponseStatus(HttpStatus.CREATED)
    private CreditCardMV create(@RequestParam(value = "primary", defaultValue = "-1", required = false) Integer primaryId,
                                @RequestParam(value = "secondary", defaultValue = "-1", required = false) Integer secondaryId,
                                @RequestBody @Valid AccountDto accountDto){

        return creditCardService.create(primaryId, secondaryId, accountDto);
    }


}
