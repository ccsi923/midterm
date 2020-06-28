package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.dto.TransactionThirdPartyRequest;

public interface ThirdPartyControllerRepo {

    public void debit(TransactionThirdPartyRequest transactionRequest);
    public void credit(TransactionThirdPartyRequest transactionRequest);
}
