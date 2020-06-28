package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.AccountHolderVM;

import java.util.List;

public interface AccountHolderController {

    public List<AccountHolderVM> findAll();
}
