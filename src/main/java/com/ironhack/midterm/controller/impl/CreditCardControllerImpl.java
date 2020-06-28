package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.CreditCardController;
import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CheckingVM;
import com.ironhack.midterm.dto.CreditCardVM;
import com.ironhack.midterm.service.CreditCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "Creditcard Controller")
@RestController
@RequestMapping("/")
public class CreditCardControllerImpl implements CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping("/creditcards")
    @ApiOperation(value = "Find all creditcard accounts",
            notes = "All creditcard accounts",
            response = CreditCardVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCardVM> findAll(){
        return creditCardService.findAll();
    }

    @PostMapping("/creditcard/")
    @ApiOperation(value = "Create a creditcard account",
            notes = "Creation of creditcard account",
            response = CreditCardVM.class)
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardVM create(@RequestParam(value = "primary", defaultValue = "-1", required = false) Integer primaryId,
                                @RequestParam(value = "secondary", defaultValue = "-1", required = false) Integer secondaryId,
                                @RequestBody @Valid AccountRequest accountRequest){

        return creditCardService.create(primaryId, secondaryId, accountRequest);
    }

}
