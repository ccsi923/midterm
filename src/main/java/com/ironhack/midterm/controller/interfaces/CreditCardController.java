package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CreditCardVM;

import java.util.List;

public interface CreditCardController {
    public List<CreditCardVM> findAll();
    public CreditCardVM create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest);

}
