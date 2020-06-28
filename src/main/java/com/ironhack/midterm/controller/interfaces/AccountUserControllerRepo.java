package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.TransactionAccountUserRequest;
import com.ironhack.midterm.model.Transaction;
import com.ironhack.midterm.model.users.User;

import java.util.List;

public interface AccountUserControllerRepo {
    public List<BalanceVM> findAllBalance(User user);
    public BalanceVM findBalanceByAcountId(User user, Integer accountId);
    public Transaction transactions(User user, TransactionAccountUserRequest transactionRequest);

}
