package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.SavingVM;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public List<SavingVM> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<SavingVM> savingVMS = new ArrayList<>();

        savingRepository.findAll().forEach(
                saving -> {
                    savingVMS.add(new SavingVM(saving.getId(),saving.getBalance(), saving.getPrimaryOwner(),
                        saving.getSecondaryOwner(), saving.getMinimumBalance(), saving.getPenaltyFee(),
                        saving.getStatus(), saving.getInterestRate(), saving.isPenalty()));
                });
        LOGGER.info("[END] - findAll");
        return savingVMS;
    }
    @Secured({"ROLE_ADMIN"})
    public SavingVM create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest){

        LOGGER.info("[INIT] - create");

        if((primaryId != -1)){
            if (accountHolderRepository.findById(primaryId).isEmpty()){
                LOGGER.error("There is not Account Holder with id " + primaryId);
                throw new WrongInput("There is not Account Holder with id " + primaryId );
             } else {
                  accountRequest.setPrimaryOwner(accountHolderRepository.findById(primaryId).get());
                }
        }

        if(secondaryId != -1){
             if((accountHolderRepository.findById(secondaryId).isEmpty())) {
                 LOGGER.error("There is not Account Holder with id " + secondaryId);
                 throw new WrongInput("There is not Account Holder with id " + secondaryId);
               } else {
                  accountRequest.setSecondaryOwner(accountHolderRepository.findById(secondaryId).get());
                }
        }
        if (primaryId == -1) {
            if (accountRequest.getPrimaryOwner() != null) {
                LOGGER.info("Searching user with name " + accountRequest.getPrimaryOwner().getAccountUser().getUsername());
                Optional<User> found = userRepo.findByUsername(accountRequest.getPrimaryOwner().getAccountUser().getUsername());

                if (found.isEmpty()) {
                    LOGGER.info("User with name " + accountRequest.getPrimaryOwner().getAccountUser().getUsername() + " not found");

                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    AccountUser newUser = new AccountUser(accountRequest.getPrimaryOwner().getAccountUser().getUsername(),
                            passwordEncoder.encode(accountRequest.getPrimaryOwner().getAccountUser().getPassword()),
                            accountRequest.getPrimaryOwner());
                    accountRequest.getPrimaryOwner().setAccountUser(newUser);
                    userRepo.save(newUser);
                    accountHolderRepository.save(accountRequest.getPrimaryOwner());
                    LOGGER.info("User with name " + accountRequest.getPrimaryOwner().getAccountUser().getUsername() + " has been added");

                    Role role = new Role("ROLE_ACCOUNTHOLDER", newUser);
                    roleRepository.save(role);


                } else {
                    LOGGER.error("The name of user " + found.get().getUsername() + " already exists");
                    throw new WrongInput("The username " + accountRequest.getPrimaryOwner().getAccountUser().getUsername() + " already exist");
                }

            } else {
                LOGGER.error("You must give a Primary Account Holder");
                throw new WrongInput("You must give a Primary Account Holder");
            }
        }

        if (secondaryId == -1) {
            if (accountRequest.getSecondaryOwner() != null) {
                Optional<User> found = userRepo.findByUsername(accountRequest.getSecondaryOwner().getAccountUser().getUsername());
                if (found.isEmpty()) {

                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    AccountUser newUser = new AccountUser(accountRequest.getSecondaryOwner().getAccountUser().getUsername(),
                            passwordEncoder.encode(accountRequest.getSecondaryOwner().getAccountUser().getPassword()),
                            accountRequest.getSecondaryOwner());
                    accountRequest.getSecondaryOwner().setAccountUser(newUser);
                    userRepo.save(newUser);
                    accountHolderRepository.save(accountRequest.getSecondaryOwner());
                    Role role = new Role("ROLE_ACCOUNTHOLDER", newUser);
                    roleRepository.save(role);

                } else {
                    throw new WrongInput("The username " + accountRequest.getSecondaryOwner().getAccountUser().getUsername() + " already exist");
                }
            }
        }

        if(accountRequest.getAmount().compareTo(new BigDecimal("1000")) < 0){
            LOGGER.error("Amount to create account must be greater than 1000");
            throw new WrongInput("Amount to create account must be greater than 1000");
        }

        if (accountRequest.getInterestRate() == null){
            LOGGER.info("InterestRate -> 0.0025");
            accountRequest.setInterestRate(new BigDecimal("0.0025").setScale(4, RoundingMode.HALF_EVEN));
        }
        if (accountRequest.getInterestRate().compareTo(new BigDecimal("0.5")) > 0){
            LOGGER.error("Interest Rate must be under 0.5");
            throw new WrongInput("Interest Rate must be under 0.5");
        }
        if (accountRequest.getMinimumBalance() == null){
            LOGGER.info("Minimum Balance -> 1000");
            accountRequest.setMinimumBalance(new BigDecimal("1000"));
        }
        if( ( accountRequest.getMinimumBalance().compareTo(new BigDecimal("100")) < 0) || ( accountRequest.getMinimumBalance().compareTo(new BigDecimal("1000")) > 0 ) ){
            LOGGER.error("Minimum Balance can't be under 100");
            throw new WrongInput("Minimum Balance can't be under 100");
        }

        Saving saving = new Saving(new Money(accountRequest.getAmount()), accountRequest.getSecretKey(), accountRequest.getPrimaryOwner(),
                accountRequest.getSecondaryOwner(), new BigDecimal("40"), Status.ACTIVE, accountRequest.getMinimumBalance(), accountRequest.getInterestRate());
        LOGGER.info("Saving account");
        savingRepository.save(saving);
        LOGGER.info("[END] - create");

        return new SavingVM(saving.getId(),saving.getBalance(), saving.getPrimaryOwner(),
                saving.getSecondaryOwner(), saving.getMinimumBalance(), saving.getPenaltyFee(),
                saving.getStatus(), saving.getInterestRate(), saving.isPenalty());
    }
}
