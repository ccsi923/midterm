package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountHolderMV;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<AccountHolderMV> findAll(){
        List<AccountHolderMV> accountHolderMVS = accountHolderRepository.findAll().stream()
                .map(accountHolder -> new AccountHolderMV(accountHolder.getId(), accountHolder.getName(),
                        accountHolder.getBirth(), accountHolder.getPrimaryAddress(), accountHolder.getMailingAddress(),
                        accountHolder.isLogged()))
                .collect(Collectors.toList());
        return accountHolderMVS;
    }

    public AccountHolderMV create(AccountHolder accountHolder){
        accountHolderRepository.save(accountHolder);
        return new AccountHolderMV(accountHolder.getId(), accountHolder.getName(),
                accountHolder.getBirth(), accountHolder.getPrimaryAddress(), accountHolder.getMailingAddress(),
                accountHolder.isLogged());
    }
}
