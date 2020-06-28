package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CheckingVM;

import java.util.List;

public interface CheckingController {

    public List<CheckingVM> findAll();
    public CheckingVM create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest);
}
