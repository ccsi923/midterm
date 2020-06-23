package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.TransactionThirdPartyRequest;
import com.ironhack.midterm.exceptions.NotEnoughFunds;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class ThirdPartyService {

    private static final Logger LOGGER = LogManager.getLogger(ThirdPartyService.class);

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

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

    @Secured({"ROLE_THIRDPARTY"})
    @Transactional
    public void debit(TransactionThirdPartyRequest transactionRequest, ThirdParty thirdParty){
        LOGGER.info("[INIT] - debit logged " + thirdParty.getUsername());

        try {
            String type = transactionRequest.getAccountType().toLowerCase().trim();
            switch (type){
                case "saving":
                    LOGGER.info("Searching Saving Account with id " + transactionRequest.getAccountId() );

                    Saving saving = savingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Saving Account Found with id " + transactionRequest.getAccountId()  );

                    if (saving.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {


                        if (((saving.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)) {

                            if ((!saving.isPenalty())
                                    &&
                                    ((saving.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(saving.getMinimumBalance()) <= 0)) {
                                saving.getBalance().decreaseAmount(transactionRequest.getAmount().add(saving.getPenaltyFee()));
                                saving.setPenalty(true);
                                LOGGER.info("Amount debited = " + transactionRequest.getAmount().add(saving.getPenaltyFee()));


                            } else {
                                saving.getBalance().decreaseAmount(transactionRequest.getAmount());
                                LOGGER.info("Amount debited = " + transactionRequest.getAmount());

                            }
                            savingRepository.save(saving);
                            LOGGER.info("New amount = " + saving.getBalance().getAmount());
                            return;

                        } else {
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount());
                        }
                    } else {
                        throw new WrongInput("Secret Key incorrect for "+ type +" account with id " + transactionRequest.getAccountId());

                    }

                case "student" :

                    LOGGER.info("Searching Student Account with id " + transactionRequest.getAccountId() );
                    StudentChecking studentChecking = studentCheckingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Student Account Found with id " + transactionRequest.getAccountId() );
                    if (studentChecking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                        if (((studentChecking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)) {

                            studentChecking.getBalance().decreaseAmount(transactionRequest.getAmount());
                            studentCheckingRepository.save(studentChecking);
                            LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                            return;
                        } else {
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount());

                        }
                    }else {
                        throw new WrongInput("Secret Key incorrect for "+ type +" account with id " + transactionRequest.getAccountId());

                    }
                case "checking" :


                    LOGGER.info("Searching Checking Account with id " + transactionRequest.getAccountId() );
                    Checking checking = checkingReposiroty.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Checking Account Found with id " + transactionRequest.getAccountId()  );
                    if (checking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                        if (((checking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)) {

                            if ((!checking.isPenalty()) && ((checking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(checking.getMinimumBalance()) <= 0)) {
                                checking.getBalance().decreaseAmount(transactionRequest.getAmount().add(checking.getPenaltyFee()));
                                checking.setPenalty(true);
                                LOGGER.info("Amount debited = " + transactionRequest.getAmount().add(checking.getPenaltyFee()));

                            } else {
                                checking.getBalance().decreaseAmount(transactionRequest.getAmount());

                            }
                            checkingReposiroty.save(checking);
                            LOGGER.info("New amount = " + checking.getBalance().getAmount());
                            return;

                        } else {
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + checking.getBalance().getAmount());
                        }
                    } else {
                        throw new WrongInput("Secret Key incorrect for "+ type +" account with id " + transactionRequest.getAccountId());

                    }
            }

        } catch (Exception e){
            throw new WrongInput("Fail Transaction");
        }
    }

    @Secured({"ROLE_THIRDPARTY"})
    @Transactional
    public void credit(TransactionThirdPartyRequest transactionRequest, ThirdParty thirdParty){
        LOGGER.info("[INIT] - credit logged " + thirdParty.getUsername());

        try {
            String type = transactionRequest.getAccountType().toLowerCase().trim();
            switch (type) {
                case "saving":

                    LOGGER.info("Searching Saving Account");
                    Saving saving = savingRepository.findById(transactionRequest.getAccountId()).orElseThrow(() -> new WrongInput("There is not " + transactionRequest.getAccountType() + " with id " + transactionRequest.getAccountId()));
                    saving.check();
                    LOGGER.info("Saving Account Found");

                    if (saving.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                        if ((saving.isPenalty()) && ((saving.getBalance().getAmount().add(transactionRequest.getAmount())).compareTo(saving.getMinimumBalance()) >= 0)) {
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
                    } else {
                        throw new WrongInput("Secret Key incorrect for "+ type +" account with id " + transactionRequest.getAccountId());

                    }

                case "student" :

                    LOGGER.info("Searching Student Account");
                    StudentChecking studentChecking = studentCheckingRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Student Account Found");
                    if (studentChecking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                        studentChecking.setBalance(new Money(studentChecking.getBalance().increaseAmount(transactionRequest.getAmount())));
                        studentCheckingRepository.save(studentChecking);
                        LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                        return;
                    } else {
                        throw new WrongInput("Secret Key incorrect for "+ type +" account with id " + transactionRequest.getAccountId());

                    }
                case "checking" :

                    LOGGER.info("Searching Checking Account");
                    Checking checking = checkingReposiroty.findById(transactionRequest.getAccountId()).orElseThrow(()-> new  WrongInput("There is not "+ transactionRequest.getAccountType() +" with id " + transactionRequest.getAccountId()));
                    LOGGER.info("Checking Account Found");

                    if (checking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {


                        if ((checking.isPenalty()) && ((checking.getBalance().getAmount().add(transactionRequest.getAmount())).compareTo(checking.getMinimumBalance()) >= 0)) {
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
                    }else {
                        throw new WrongInput("Secret Key incorrect for "+ type +" account with id " + transactionRequest.getAccountId());

                    }
            }

        } catch (Exception e){
            throw new WrongInput("Fail Transaction");
        }
    }


}
