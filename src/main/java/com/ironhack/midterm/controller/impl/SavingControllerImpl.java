package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.SavingController;
import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.SavingMV;
import com.ironhack.midterm.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SavingControllerImpl implements SavingController {

    @Autowired
    private SavingService savingService;

    @GetMapping("/savings")
    @ResponseStatus(HttpStatus.OK)
    public List<SavingMV> findAll(){
        return savingService.findAll();
    }

    @PostMapping("/saving")
    @ResponseStatus(HttpStatus.CREATED)
    private SavingMV create(@RequestParam(value = "primary", defaultValue = "-1", required = false) Integer primaryId,
                          @RequestParam(value = "secondary", defaultValue = "-1", required = false) Integer secondaryId,
                          @RequestBody @Valid AccountRequest accountRequest){

        return savingService.create(primaryId, secondaryId, accountRequest);
    }

}
