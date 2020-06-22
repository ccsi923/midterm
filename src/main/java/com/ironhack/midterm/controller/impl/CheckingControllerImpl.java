package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.CheckingController;
import com.ironhack.midterm.dto.AccountDto;
import com.ironhack.midterm.dto.CheckingMV;
import com.ironhack.midterm.dto.CreditCardMV;
import com.ironhack.midterm.service.CheckingService;
import com.ironhack.midterm.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CheckingControllerImpl implements CheckingController {

    @Autowired
    private CheckingService checkingService;

    @GetMapping("/checkings")
    @ResponseStatus(HttpStatus.OK)
    public List<CheckingMV> findAll(){
        return checkingService.findAll();
    }

    @PostMapping("/checking/")
    @ResponseStatus(HttpStatus.CREATED)
    private CheckingMV create(@RequestParam(value = "primary", defaultValue = "-1", required = false) Integer primaryId,
                                @RequestParam(value = "secondary", defaultValue = "-1", required = false) Integer secondaryId,
                                @RequestBody @Valid AccountDto accountDto){

        return checkingService.create(primaryId, secondaryId, accountDto);
    }

}
