package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.AccessAccountRequest;
import com.ironhack.midterm.dto.AccountAdminAccess;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.UserRequest;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll(){
        return adminService.findAll();
    }


    @GetMapping("/account/admin")
    public AccountAdminAccess findById(@RequestBody @Valid AccessAccountRequest accessAccountRequest){

        return adminService.findById(accessAccountRequest);
    }

    /**  CREDIT  **/

    @PostMapping("/debit/admin/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@RequestBody @Valid TransactionRequest transactionRequest){
         adminService.debit(transactionRequest);
    }


    /**  DEBIT  **/

    @PostMapping("/credit/admin/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@RequestBody @Valid TransactionRequest transactionRequest){
        adminService.credit(transactionRequest);
    }
    /** Creation of ThirdParty **/

    @PostMapping("/user/thirdparty")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty create(@RequestBody @Valid UserRequest userRequest){
        return adminService.create(userRequest);
    }



}
