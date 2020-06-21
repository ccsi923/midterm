package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.StudentCheckingController;
import com.ironhack.midterm.dto.CreditCardMV;
import com.ironhack.midterm.dto.StudentCheckingMV;
import com.ironhack.midterm.model.StudentChecking;
import com.ironhack.midterm.service.CheckingService;
import com.ironhack.midterm.service.StudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentCheckingControllerImpl implements StudentCheckingController {

    @Autowired
    private StudentCheckingService studentCheckingService;

    @GetMapping("/students")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentCheckingMV> findAll(){
        return studentCheckingService.findAll();
    }
}
