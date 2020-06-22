package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.AccessAccountDTO;
import com.ironhack.midterm.dto.AccountAdminAccess;
import com.ironhack.midterm.dto.DebitDto;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.model.Account;
import com.ironhack.midterm.model.Transaction;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.service.AdminService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public AccountAdminAccess findById(@RequestBody @Valid AccessAccountDTO accessAccountDTO){

        return adminService.findById(accessAccountDTO);
    }

    /**  CREDIT  **/

    @PostMapping("/debit/admin/")
    @ResponseStatus(HttpStatus.CREATED)
    public void debit(@RequestBody @Valid DebitDto debitDto){
         adminService.debit(debitDto);
    }


    /**  DEBIT  **/

    @PostMapping("/credit/admin/")
    @ResponseStatus(HttpStatus.CREATED)
    public void credit(@RequestBody @Valid DebitDto debitDto){
        adminService.credit(debitDto);
    }
    /** Creation of ThirdParty **/

    @PostMapping("/user/thirdparty")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty create(@RequestBody @Valid UserDTO userDTO){
        return adminService.create(userDTO);
    }



}
