package com.ironhack.midterm.service;


import com.ironhack.midterm.dto.AccessAccountDTO;
import com.ironhack.midterm.dto.AccountAdminAccess;
import com.ironhack.midterm.dto.DebitDto;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.exceptions.NotEnoughFunds;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    private static final Logger LOGGER = LogManager.getLogger(AdminService.class);


    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CheckingReposiroty checkingReposiroty;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;


    @Secured({"ROLE_ADMIN"})
    public ThirdParty create(UserDTO userDTO){
        LOGGER.info("[INIT] - create");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ThirdParty newUser = new ThirdParty(userDTO.getUserName(),passwordEncoder.encode(userDTO.getPassword()), userDTO.getHaskey());
        userRepo.save(newUser);
        Role role = new Role("ROLE_THIRDPARTY",newUser);
        roleRepository.save(role);
        return newUser;

    }

    @Secured({"ROLE_ADMIN"})
    public AccountAdminAccess findById(AccessAccountDTO accessAccountDTO){
        LOGGER.info("[INIT] - findById");

        try {
            String type = accessAccountDTO.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":
                    Saving saving = savingRepository.findById(accessAccountDTO.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountDTO.getAccountType() +" with id " + accessAccountDTO.getId()));
                    return  new AccountAdminAccess(saving.getId(), saving.getPrimaryOwner().getName(), saving.getBalance());
                case "creditcard" :
                    CreditCard creditCard = creditCardRepository.findById(accessAccountDTO.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountDTO.getAccountType() +" with id " + accessAccountDTO.getId()));
                    return  new AccountAdminAccess(creditCard.getId(), creditCard.getPrimaryOwner().getName(), creditCard.getBalance());
                case "student" :
                    StudentChecking studentChecking = studentCheckingRepository.findById(accessAccountDTO.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountDTO.getAccountType() +" with id " + accessAccountDTO.getId()));
                    return  new AccountAdminAccess(studentChecking.getId(), studentChecking.getPrimaryOwner().getName(), studentChecking.getBalance());
                case "checking" :
                    Checking checking = checkingReposiroty.findById(accessAccountDTO.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountDTO.getAccountType() +" with id " + accessAccountDTO.getId()));
                    return  new AccountAdminAccess(checking.getId(), checking.getPrimaryOwner().getName(), checking.getBalance());
            }

        } catch (Exception e){
            throw new WrongInput("There is not "+ accessAccountDTO.getAccountType() +" with id " + accessAccountDTO.getId());
        } return null;
    }


    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void debit(DebitDto debitDto){
        LOGGER.info("[INIT] - debit");

        try {
            String type = debitDto.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":
                    LOGGER.info("Searching Saving Account");

                    Saving saving = savingRepository.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    LOGGER.info("Saving Account Found");

                    if ( (saving.getBalance().decreaseAmount(debitDto.getAmount()).compareTo(new BigDecimal("0")) >= 0)) {
                        saving.setBalance(new Money(saving.getBalance().decreaseAmount(debitDto.getAmount())));
                    }else {
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount() );
                    }
                    return;
                case "creditcard" :
                    CreditCard creditCard = creditCardRepository.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    creditCard.setBalance( new Money (creditCard.getBalance().decreaseAmount(debitDto.getAmount())) );
                    return;
                case "student" :
                    StudentChecking studentChecking = studentCheckingRepository.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    studentChecking.setBalance( new Money (studentChecking.getBalance().decreaseAmount(debitDto.getAmount())) );
                    return;
                case "checking" :
                    Checking checking = checkingReposiroty.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    checking.setBalance( new Money (checking.getBalance().decreaseAmount(debitDto.getAmount())) );

            }

        } catch (Exception e){
            throw new WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId());
        }
    }

    @Secured({"ROLE_ADMIN"})
    //@Transactional
    public void credit(DebitDto debitDto){
        LOGGER.info("[INIT] - credit");

        try {
            String type = debitDto.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":
                    LOGGER.info("Searching Saving Account");
                    Saving saving = savingRepository.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    LOGGER.info("Saving Account Found");
                    saving.setBalance( new Money (saving.getBalance().increaseAmount(debitDto.getAmount())) );
                    return;
                case "creditcard" :
                    CreditCard creditCard = creditCardRepository.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    creditCard.setBalance( new Money (creditCard.getBalance().increaseAmount(debitDto.getAmount())) );
                case "student" :
                    StudentChecking studentChecking = studentCheckingRepository.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    studentChecking.setBalance( new Money (studentChecking.getBalance().increaseAmount(debitDto.getAmount())) );
                case "checking" :
                    Checking checking = checkingReposiroty.findById(debitDto.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId()));
                    checking.setBalance( new Money (checking.getBalance().increaseAmount(debitDto.getAmount())) );
            }

        } catch (Exception e){
            throw new WrongInput("There is not "+ debitDto.getAccountType() +" with id " + debitDto.getAccountId());
        }
    }



    @Secured({"ROLE_ADMIN"})
    public List<User> findAll(){
        return userRepo.findAll();
    }
}
