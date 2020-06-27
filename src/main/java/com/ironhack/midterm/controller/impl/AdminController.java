package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.*;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "Admin Controller")
@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @ApiOperation(value = "Find all users",
            notes = "All users",
            response = BalanceVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll(){
        return adminService.findAll();
    }


    @GetMapping("/account/admin")
    @ApiOperation(value = "Find account by id being admin",
            notes = "Admin find an account by his id",
            response = AccountAdminAccess.class)
    @ResponseStatus(HttpStatus.OK)
    public AccountAdminAccess findById(@RequestBody @Valid AccessAccountRequest accessAccountRequest){

        return adminService.findById(accessAccountRequest);
    }

    /**  Debit  **/

    @PostMapping("/debit/admin/")
    @ApiOperation(value = "Debit an account being admin",
            notes = "Admin debit an account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@RequestBody @Valid TransactionRequest transactionRequest){
         adminService.debit(transactionRequest);
    }


    /**  Credit  **/

    @PostMapping("/credit/admin/")
    @ApiOperation(value = "Credit an account being admin",
            notes = "Admin credit an account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@RequestBody @Valid TransactionRequest transactionRequest){
        adminService.credit(transactionRequest);
    }
    /** Creation of ThirdParty **/

    @PostMapping("/user/thirdparty")
    @ApiOperation(value = "Create a thirdparty user being admin",
            notes = "Admin create a thirdparty user")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty create(@RequestBody @Valid UserRequest userRequest){
        return adminService.create(userRequest);
    }

    /** Removing status frozen **/

    @PatchMapping("/admin/remove/frozen/{id}")
    @ApiOperation(value = "Remove frozen status to an user being admin",
            notes = "Admin remove status frozen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFrozen(@PathVariable("id") Integer acccountId){
        adminService.removeFrozen(acccountId);
    }

}
