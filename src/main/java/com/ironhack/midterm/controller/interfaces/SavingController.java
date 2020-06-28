package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.SavingVM;

import java.util.List;

public interface SavingController {


    public List<SavingVM> findAll();
    public SavingVM create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest);
}
