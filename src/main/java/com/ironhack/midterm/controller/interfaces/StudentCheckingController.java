package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.StudentCheckingVM;

import java.util.List;

public interface StudentCheckingController {
    public List<StudentCheckingVM> findAll();
}
