package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountHolderVM;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountHolderService {

    private static final Logger LOGGER = LogManager.getLogger(AccountHolderService.class);


    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Secured({"ROLE_ADMIN"})
    public List<AccountHolderVM> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<AccountHolderVM> accountHolderVMS = accountHolderRepository.findAll().stream()
                .map(accountHolder -> new AccountHolderVM(accountHolder.getId(), accountHolder.getName(),
                        accountHolder.getBirth(), accountHolder.getPrimaryAddress(), accountHolder.getMailingAddress()
                        ))
                .collect(Collectors.toList());
        LOGGER.info("[END] - findAll");
        return accountHolderVMS;
    }
    /*@Secured({"ROLE_ADMIN"})
    public AccountHolderMV create(AccountHolder accountHolder){
        Optional<User> found = userRepo.findByUsername(accountHolder.getAccountUser().getUsername());

        if (found.isEmpty()) {

        accountHolderRepository.save(accountHolder);
        return new AccountHolderMV(accountHolder.getId(), accountHolder.getName(),
                accountHolder.getBirth(), accountHolder.getPrimaryAddress(), accountHolder.getMailingAddress(),
                accountHolder.isLogged());
         } else {
        LOGGER.error("You must give a Primary Account Holder");
        throw new WrongInput("You must give a Primary Account Holder");
        }
    }*/

}
