package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.TransactionThirdPartyRequest;
import com.ironhack.midterm.exceptions.NotEnoughFunds;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.ThirdParty;
import com.ironhack.midterm.model.users.User;
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

    @Autowired
    private AccountRepository accountRepository;


    @Secured({"ROLE_THIRDPARTY"})
    @Transactional
    public void debit(TransactionThirdPartyRequest transactionRequest){
        LOGGER.info("[INIT] - debit logged ");
        LOGGER.info("Searching account");
        Account account = accountRepository.findById(transactionRequest.getAccountId()).orElseThrow(() -> new WrongInput("Account with id "+ transactionRequest.getAccountId() + " not found"));
        LOGGER.info("Account found");
        if(account instanceof Checking) {
            ((Checking) account).check();
            Checking checking = (Checking) account;
            if (checking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                if (((checking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)) {

                    if ((!checking.isPenalty()) && ((checking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(checking.getMinimumBalance()) <= 0)) {
                        checking.getBalance().decreaseAmount(transactionRequest.getAmount().add(checking.getPenaltyFee()));
                        checking.setPenalty(true);
                        LOGGER.info("Account has been penalized");
                        LOGGER.info("Amount debited = " + transactionRequest.getAmount().add(checking.getPenaltyFee()));

                    } else {
                        checking.getBalance().decreaseAmount(transactionRequest.getAmount());

                    }
                    checkingReposiroty.save(checking);
                    LOGGER.info("New amount = " + checking.getBalance().getAmount());
                    LOGGER.info("[END] - debit logged ");
                    return;

                } else {
                    LOGGER.error("There is not enough funds. Actual balance: " + checking.getBalance().getAmount());
                    throw new NotEnoughFunds("There is not enough funds. Actual balance: " + checking.getBalance().getAmount());
                }
            } else {
                LOGGER.error("Secret Key incorrect for checking account with id " + transactionRequest.getAccountId());
                throw new WrongInput("Secret Key incorrect for checking account with id " + transactionRequest.getAccountId());
            }
        }
            if (account instanceof StudentChecking) {
                LOGGER.info("Searching Student Account");
                StudentChecking studentChecking = ((StudentChecking) account);
                if (studentChecking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                    if (((studentChecking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)) {

                        studentChecking.getBalance().decreaseAmount(transactionRequest.getAmount());
                        studentCheckingRepository.save(studentChecking);
                        LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                        LOGGER.info("[END] - debit logged ");
                        return;
                    } else {
                        LOGGER.error("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount());
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount());

                    }
                } else {
                    LOGGER.error("Secret Key incorrect for student account with id " + transactionRequest.getAccountId());
                    throw new WrongInput("Secret Key incorrect for student account with id " + transactionRequest.getAccountId());
                }
            }
                if (account instanceof Saving) {
                    ((Saving) account).check();
                    Saving saving = (Saving) account;

                    if (saving.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {


                        if (((saving.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0)) {

                            if ((!saving.isPenalty())
                                    &&
                                    ((saving.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(saving.getMinimumBalance()) <= 0)) {
                                saving.getBalance().decreaseAmount(transactionRequest.getAmount().add(saving.getPenaltyFee()));
                                saving.setPenalty(true);
                                LOGGER.info("Account has been penalized");
                                LOGGER.info("Amount debited = " + transactionRequest.getAmount().add(saving.getPenaltyFee()));


                            } else {
                                saving.getBalance().decreaseAmount(transactionRequest.getAmount());
                                LOGGER.info("Amount debited = " + transactionRequest.getAmount());

                            }

                            savingRepository.save(saving);
                            LOGGER.info("New amount = " + saving.getBalance().getAmount());
                            LOGGER.info("[END] - debit logged ");
                            return;

                        } else {
                            LOGGER.error("There is not enough funds. Actual balance: " + saving.getBalance().getAmount());
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount());
                        }
                    } else {
                        LOGGER.error("Secret Key incorrect for saving account with id " + transactionRequest.getAccountId());
                        throw new WrongInput("Secret Key incorrect for saving account with id " + transactionRequest.getAccountId());
                    }
                }
            }


    @Secured({"ROLE_THIRDPARTY"})
    @Transactional
    public void credit(TransactionThirdPartyRequest transactionRequest){
        LOGGER.info("[INIT] - credit logged ");
            Account account = accountRepository.findById(transactionRequest.getAccountId()).orElseThrow(() -> new WrongInput("Account with id "+ transactionRequest.getAccountId() + " not found"));
        if(account instanceof Checking){
            ((Checking) account).check();
            LOGGER.info("Searching Checking Account");
            Checking checking =  (Checking) account;
            LOGGER.info("Checking Account Found");

            if (checking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                if ((checking.isPenalty()) && ((checking.getBalance().getAmount().add(transactionRequest.getAmount())).compareTo(checking.getMinimumBalance()) >= 0)) {
                    checking.getBalance().increaseAmount(transactionRequest.getAmount());
                    checking.setPenalty(false);
                    LOGGER.info("Penalization has been removed");
                    LOGGER.info("Added = " + transactionRequest.getAmount());
                    LOGGER.info("New amount = " + checking.getBalance().getAmount());

                } else {
                    checking.getBalance().increaseAmount(transactionRequest.getAmount());
                    LOGGER.info("New amount = " + checking.getBalance().getAmount());
                }
                checkingReposiroty.save(checking);
                LOGGER.info("[END] - credit logged");
                return;
            }else {
                LOGGER.error("Secret Key incorrect for checking account with id " + transactionRequest.getAccountId());
                throw new WrongInput("Secret Key incorrect for checking account with id " + transactionRequest.getAccountId());
            }
        }
        if(account instanceof StudentChecking){
            LOGGER.info("Searching Student Account");
            StudentChecking studentChecking = ((StudentChecking) account);
            LOGGER.info("Student Account Found");
            if (studentChecking.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                studentChecking.setBalance(new Money(studentChecking.getBalance().increaseAmount(transactionRequest.getAmount())));
                studentCheckingRepository.save(studentChecking);
                LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                LOGGER.info("[END] - credit logged");
                return;
            } else {
                LOGGER.error("Secret Key incorrect for student account with id " + transactionRequest.getAccountId());
                throw new WrongInput("Secret Key incorrect for student account with id " + transactionRequest.getAccountId());
            }
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            Saving saving =  (Saving) account;
            if (saving.getSecretKey().equals(transactionRequest.getSecretKey().trim())) {

                if ((saving.isPenalty()) && ((saving.getBalance().getAmount().add(transactionRequest.getAmount())).compareTo(saving.getMinimumBalance()) >= 0)) {
                    saving.getBalance().increaseAmount(transactionRequest.getAmount());
                    saving.setPenalty(false);
                    LOGGER.info("Penalization has been removed");
                    LOGGER.info("Added = " + transactionRequest.getAmount());
                    LOGGER.info("New amount = " + saving.getBalance().getAmount());
                } else {
                    saving.getBalance().increaseAmount(transactionRequest.getAmount());
                    LOGGER.info("New amount = " + saving.getBalance().getAmount());
                }
                savingRepository.save(saving);
                LOGGER.info("[END] - credit logged");
                return;
            } else {
                LOGGER.error("Secret Key incorrect for saving account with id " + transactionRequest.getAccountId());
                throw new WrongInput("Secret Key incorrect for saving account with id " + transactionRequest.getAccountId());

            }
        }

    }


}
