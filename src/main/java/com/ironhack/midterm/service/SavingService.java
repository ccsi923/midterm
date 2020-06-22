package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountDto;
import com.ironhack.midterm.dto.SavingMV;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.Saving;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.RoleRepository;
import com.ironhack.midterm.repository.SavingRepository;
import com.ironhack.midterm.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingService {

    private static final Logger LOGGER = LogManager.getLogger(SavingService.class);



    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Secured({"ROLE_ADMIN"})
    public List<SavingMV> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<SavingMV> savingMVS = savingRepository.findAll().stream().map(
                saving -> new SavingMV(saving.getId(),saving.getBalance(), saving.getPrimaryOwner(), saving.getSecondaryOwner(),
                        saving.getPenaltyFee(), saving.getInterestRate(), saving.getStatus(), saving.getInterestRate())
        ).collect(Collectors.toList());
        LOGGER.info("[END] - findAll");
        return savingMVS;
    }

    public SavingMV create(Integer primaryId, Integer secondaryId, AccountDto accountDto){

        LOGGER.info("[INIT] - create");

        if((primaryId != -1)){
            if (accountHolderRepository.findById(primaryId).isEmpty()){
                LOGGER.error("There is not Account Holder with id " + primaryId);
                throw new WrongInput("There is not Account Holder with id " + primaryId );
             } else {
                  accountDto.setPrimaryOwner(accountHolderRepository.findById(primaryId).get());
                }
        }

        if(secondaryId != -1){
             if((accountHolderRepository.findById(secondaryId).isEmpty())) {
                 LOGGER.error("There is not Account Holder with id " + secondaryId);
                 throw new WrongInput("There is not Account Holder with id " + secondaryId);
               } else {
                  accountDto.setSecondaryOwner(accountHolderRepository.findById(secondaryId).get());
                }
        }
        if (primaryId == -1){
             if (accountDto.getPrimaryOwner() != null) {
                 accountHolderRepository.save(accountDto.getPrimaryOwner());
                 PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                 AccountUser newUser = new AccountUser(accountDto.getPrimaryOwner().getName(),passwordEncoder.encode(accountDto.getPrimaryOwner().getPassword()));
                 userRepo.save(newUser);
                 Role role = new Role("ROLE_ACCOUNTHOLDER",newUser);
                 roleRepository.save(role);
             } else {
                 LOGGER.error("You must give a Parimary Account Holder" );
                 throw new WrongInput("You must give a Parimary Account Holder");
                }
        }

        if(secondaryId == -1){
            if (accountDto.getSecondaryOwner() != null){
                accountHolderRepository.save(accountDto.getSecondaryOwner());
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                AccountUser newUser = new AccountUser(accountDto.getSecondaryOwner().getName(),passwordEncoder.encode(accountDto.getSecondaryOwner().getPassword()));
                userRepo.save(newUser);
                Role role = new Role("ROLE_ACCOUNTHOLDER",newUser);
                roleRepository.save(role);

            }
        }

        if (accountDto.getInterestRate() == null){
            LOGGER.debug("InterestRate -> 0.0025");
            accountDto.setInterestRate(new BigDecimal("0.0025").setScale(4, RoundingMode.HALF_EVEN));
        }
        if (accountDto.getInterestRate().compareTo(new BigDecimal("0.5")) > 0){
            LOGGER.error("Interest Rate must be under 0.5");
            throw new WrongInput("Interest Rate must be under 0.5");
        }
        if (accountDto.getMinimumBalance() == null){
            LOGGER.debug("Minimum Balance -> 1000");
            accountDto.setMinimumBalance(new BigDecimal("1000"));
        }
        if( ( accountDto.getMinimumBalance().compareTo(new BigDecimal("100")) < 0) || ( accountDto.getMinimumBalance().compareTo(new BigDecimal("1000")) > 0 ) ){
            LOGGER.error("Minimum Balance can't be under 100");
            throw new WrongInput("Minimum Balance can't be under 100");
        }

        Saving saving = new Saving(new Money(accountDto.getAmount()), accountDto.getSecretKey(), accountDto.getPrimaryOwner(),
                accountDto.getSecondaryOwner(), new BigDecimal("40"), Status.ACTIVE, accountDto.getMinimumBalance(),accountDto.getInterestRate());
        LOGGER.info("Saving -> account Saving");
        savingRepository.save(saving);
        LOGGER.info("[END] - create");

        return new SavingMV(saving.getId(), saving.getBalance(), saving.getPrimaryOwner(), saving.getSecondaryOwner(),saving.getMinimumBalance(), saving.getPenaltyFee(),
                    saving.getStatus(), saving.getInterestRate());
    }
}
