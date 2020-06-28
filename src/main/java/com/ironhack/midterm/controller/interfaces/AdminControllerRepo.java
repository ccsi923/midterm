package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.AccountAdminAccess;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.UserRequest;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;

import java.util.List;

public interface AdminControllerRepo {

    public List<User> findAll();
    public AccountAdminAccess findById(Integer acId);
    public void debit(TransactionRequest transactionRequest);
    public void credit(TransactionRequest transactionRequest);
    public ThirdParty create(UserRequest userRequest);
    public void removeFrozen(Integer accountId);
}
