package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.SavingController;
import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CreditCardVM;
import com.ironhack.midterm.dto.SavingVM;
import com.ironhack.midterm.service.SavingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Saving Controller")
@RestController
@RequestMapping("/")
public class SavingControllerImpl implements SavingController {

    @Autowired
    private SavingService savingService;

    @GetMapping("/savings")
    @ApiOperation(value = "Find all saving accounts",
            notes = "All saving accounts",
            response = SavingVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<SavingVM> findAll(){
        return savingService.findAll();
    }

    @PostMapping("/saving/")
    @ApiOperation(value = "Create a saving account",
            notes = "Creation of saving account",
            response = SavingVM.class)
    @ResponseStatus(HttpStatus.CREATED)
    public SavingVM create(@RequestParam(value = "primary", defaultValue = "-1", required = false) Integer primaryId,
                            @RequestParam(value = "secondary", defaultValue = "-1", required = false) Integer secondaryId,
                            @RequestBody @Valid AccountRequest accountRequest){

        return savingService.create(primaryId, secondaryId, accountRequest);
    }

}
