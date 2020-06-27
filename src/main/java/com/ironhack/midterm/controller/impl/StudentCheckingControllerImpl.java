package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.StudentCheckingController;
import com.ironhack.midterm.dto.SavingVM;
import com.ironhack.midterm.dto.StudentCheckingVM;
import com.ironhack.midterm.service.StudentCheckingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Student Controller")
@RestController
@RequestMapping("/")
public class StudentCheckingControllerImpl implements StudentCheckingController {

    @Autowired
    private StudentCheckingService studentCheckingService;

    @GetMapping("/students")
    @ApiOperation(value = "Find all student accounts",
            notes = "All student accounts",
            response = StudentCheckingVM.class)
    @ResponseStatus(HttpStatus.OK)
    public List<StudentCheckingVM> findAll(){
        return studentCheckingService.findAll();
    }

}
