package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.CheckingController;
import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.CheckingVM;
import com.ironhack.midterm.service.CheckingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Checking Controller")
@RestController
@RequestMapping("/")
public class CheckingControllerImpl implements CheckingController {

    @Autowired
    private CheckingService checkingService;

    @GetMapping("/checkings")
    @ApiOperation(value = "Find all checking accounts",
            notes = "All checking accounts",
            response = CheckingVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<CheckingVM> findAll(){
        return checkingService.findAll();
    }


    @PostMapping("/checking/")
    @ApiOperation(value = "Create a checking/student account",
            notes = "Creations of checking/student account",
            response = CheckingVM.class)
    @ResponseStatus(HttpStatus.CREATED)
    public CheckingVM create(@RequestParam(value = "primary", defaultValue = "-1", required = false) Integer primaryId,
                              @RequestParam(value = "secondary", defaultValue = "-1", required = false) Integer secondaryId,
                              @RequestBody @Valid AccountRequest accountRequest){

        return checkingService.create(primaryId, secondaryId, accountRequest);
    }

}
