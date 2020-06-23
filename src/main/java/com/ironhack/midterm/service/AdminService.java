package com.ironhack.midterm.service;


import com.ironhack.midterm.dto.AccessAccountRequest;
import com.ironhack.midterm.dto.AccountAdminAccess;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.UserRequest;
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
    public ThirdParty create(UserRequest userRequest){
        LOGGER.info("[INIT] - create");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ThirdParty newUser = new ThirdParty(userRequest.getUserName(),passwordEncoder.encode(userRequest.getPassword()));
        userRepo.save(newUser);
        Role role = new Role("ROLE_THIRDPARTY", newUser);
        roleRepository.save(role);
        return newUser;

    }

    @Secured({"ROLE_ADMIN"})
    public AccountAdminAccess findById(AccessAccountRequest accessAccountRequest){
        LOGGER.info("[INIT] - findById");

        try {
            String type = accessAccountRequest.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":
                    Saving saving = savingRepository.findById(accessAccountRequest.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountRequest.getAccountType() +" with id " + accessAccountRequest.getId()));
                    return  new AccountAdminAccess(saving.getId(), saving.getPrimaryOwner().getName(), saving.getBalance());
                case "creditcard" :
                    CreditCard creditCard = creditCardRepository.findById(accessAccountRequest.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountRequest.getAccountType() +" with id " + accessAccountRequest.getId()));
                    return  new AccountAdminAccess(creditCard.getId(), creditCard.getPrimaryOwner().getName(), creditCard.getBalance());
                case "student" :
                    StudentChecking studentChecking = studentCheckingRepository.findById(accessAccountRequest.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountRequest.getAccountType() +" with id " + accessAccountRequest.getId()));
                    return  new AccountAdminAccess(studentChecking.getId(), studentChecking.getPrimaryOwner().getName(), studentChecking.getBalance());
                case "checking" :
                    Checking checking = checkingReposiroty.findById(accessAccountRequest.getId()).orElseThrow(()-> new  WrongInput("There is not "+ accessAccountRequest.getAccountType() +" with id " + accessAccountRequest.getId()));
                    return  new AccountAdminAccess(checking.getId(), checking.getPrimaryOwner().getName(), checking.getBalance());
            }

        } catch (Exception e){
            throw new WrongInput("There is not "+ accessAccountRequest.getAccountType() +" with id " + accessAccountRequest.getId());
        } return null;
    }


    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void debit(TransactionRequest transactionRequest){
        LOGGER.info("[INIT] - debit");

        try {
            String type = transactionRequest.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":
                    LOGGER.info("Searching Saving Account with id " + transactionRequest.getAccountId() );
                    Saving saving = savingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Saving Account Found with id " + transactionRequest.getAccountId()  );

                    if ( ( (saving.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)  ) {

                        if  ( (!saving.isPenalty())
                                              &&
                            ( (saving.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(saving.getMinimumBalance())  <= 0 )  ){
                                saving.getBalance().decreaseAmount(transactionRequest.getAmount().add(saving.getPenaltyFee()) );
                                saving.setPenalty(true);
                                LOGGER.info("Amount debited = " + transactionRequest.getAmount().add(saving.getPenaltyFee()));


                        } else {
                           saving.getBalance().decreaseAmount(transactionRequest.getAmount());
                            LOGGER.info("Amount debited = " + transactionRequest.getAmount());

                        }
                        savingRepository.save(saving);
                        LOGGER.info("New amount = " + saving.getBalance().getAmount());
                        return;

                    }

                    else {
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount() );
                    }

                case "creditcard" :

                    LOGGER.info("Searching CreditCard Account with id " + transactionRequest.getAccountId() );
                    CreditCard creditCard = creditCardRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("CreditCard Account Found with id " + transactionRequest.getAccountId()  );

                    if (  (creditCard.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0 ) {

                        creditCard.getBalance().decreaseAmount(transactionRequest.getAmount());
                        creditCardRepository.save(creditCard);
                        LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                        return;
                    } else {
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + creditCard.getBalance().getAmount() );

                    }

                case "student" :

                    LOGGER.info("Searching Student Account with id " + transactionRequest.getAccountId() );
                    StudentChecking studentChecking = studentCheckingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Student Account Found with id " + transactionRequest.getAccountId() );

                    if( ( (studentChecking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0) ) {

                        studentChecking.getBalance().decreaseAmount(transactionRequest.getAmount());
                        studentCheckingRepository.save(studentChecking);
                        LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                        return;
                    } else {
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount() );

                    }

                case "checking" :


                    LOGGER.info("Searching Checking Account with id " + transactionRequest.getAccountId() );
                    Checking checking = checkingReposiroty.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Checking Account Found with id " + transactionRequest.getAccountId()  );

                    if ( ( (checking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)  ) {

                        if  ( (!checking.isPenalty()) && ( (checking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(checking.getMinimumBalance())  <= 0 )  ){
                            checking.getBalance().decreaseAmount(transactionRequest.getAmount().add(checking.getPenaltyFee()) );
                            checking.setPenalty(true);
                            LOGGER.info("Amount debited = " + transactionRequest.getAmount().add(checking.getPenaltyFee()));

                        } else {
                          checking.getBalance().decreaseAmount(transactionRequest.getAmount());

                        }
                        checkingReposiroty.save(checking);
                        LOGGER.info("New amount = " + checking.getBalance().getAmount());
                        return;

                    }
                    else {
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + checking.getBalance().getAmount() );
                    }
            }

        } catch (Exception e){
            throw new WrongInput("Fail Transaction");
        }
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void credit(TransactionRequest transactionRequest){
        LOGGER.info("[INIT] - credit");

        try {
            String type = transactionRequest.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":

                    LOGGER.info("Searching Saving Account");
                    Saving saving = savingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    saving.check();
                    LOGGER.info("Saving Account Found");
                    if ( ( saving.isPenalty() ) && ( (saving.getBalance().getAmount().add(transactionRequest.getAmount())).compareTo(saving.getMinimumBalance())  >= 0 ) ) {
                        saving.getBalance().increaseAmount(transactionRequest.getAmount());
                        saving.setPenalty(false);
                        LOGGER.info("Added = " + transactionRequest.getAmount());
                        LOGGER.info("New amount = " + saving.getBalance().getAmount());
                    } else {
                        saving.getBalance().increaseAmount(transactionRequest.getAmount());
                        LOGGER.info("New amount = " + saving.getBalance().getAmount());
                    }
                    savingRepository.save(saving);
                    return;
                case "creditcard" :

                    LOGGER.info("Searching CreditCard Account");
                    CreditCard creditCard = creditCardRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    creditCard.check();
                    LOGGER.info("CreditCard Account Found");

                    creditCard.setBalance( new Money (creditCard.getBalance().increaseAmount(transactionRequest.getAmount())) );
                    creditCardRepository.save(creditCard);
                    LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                    return;
                case "student" :

                    LOGGER.info("Searching Student Account");
                    StudentChecking studentChecking = studentCheckingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Student Account Found");

                    studentChecking.setBalance( new Money (studentChecking.getBalance().increaseAmount(transactionRequest.getAmount())) );
                    studentCheckingRepository.save(studentChecking);
                    LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                    return;
                case "checking" :

                    LOGGER.info("Searching Checking Account");
                    Checking checking = checkingReposiroty.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Checking Account Found");
                    if ( ( checking.isPenalty() ) && ( (checking.getBalance().getAmount().add(transactionRequest.getAmount())).compareTo(checking.getMinimumBalance())  >= 0 ) ) {
                        checking.getBalance().increaseAmount(transactionRequest.getAmount());
                        checking.setPenalty(false);
                        LOGGER.info("Added = " + transactionRequest.getAmount());
                        LOGGER.info("New amount = " + checking.getBalance().getAmount());

                    } else {
                        checking.getBalance().increaseAmount(transactionRequest.getAmount());
                        LOGGER.info("New amount = " + checking.getBalance().getAmount());
                    }
                    checkingReposiroty.save(checking);
                    return;
            }

        } catch (Exception e){
            throw new WrongInput("Fail Transaction");
        }
    }


    @Secured({"ROLE_ADMIN"})
    public List<User> findAll(){
        return userRepo.findAll();
    }
}
