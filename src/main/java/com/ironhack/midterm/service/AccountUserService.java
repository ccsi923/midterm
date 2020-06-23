package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.TransactionAccountUserRequest;
import com.ironhack.midterm.exceptions.NotEnoughFunds;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountUserService {

    private static final Logger LOGGER = LogManager.getLogger(AccountUserService.class);


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
    private AccountUserRepository  accountUserRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Secured({"ROLE_ACCOUNTHOLDER"})
    @Transactional
    public Transaction transactions(TransactionAccountUserRequest transactionAccountUserRequest, User user){
        String originType = transactionAccountUserRequest.getTypeOriginAccount().toLowerCase().trim();
        String destineType = transactionAccountUserRequest.getTypeDestineAccount().toLowerCase().trim();
        Integer originId = transactionAccountUserRequest.getOwnAccountId();
        Integer destineId = transactionAccountUserRequest.getForeignAccountId();
        BigDecimal amount = transactionAccountUserRequest.getAmount();
        String receptorName = transactionAccountUserRequest.getReceptorName();
        Transaction transaction = new Transaction();

        switch (originType) {
            case "saving":
                LOGGER.info("Searching Saving Account");
                Saving saving = savingRepository.findById(originId).orElseThrow(()-> new WrongInput("There is not "+ originType +" with id " + originId));
                saving.check();
                LOGGER.info("Saving Account Found with id "+ originId);
                if ( ( (saving.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0)  ) {

                    if  ( (!saving.isPenalty())
                            &&
                            ( (saving.getBalance().getAmount().subtract(amount)).compareTo(saving.getMinimumBalance())  <= 0 )  ){
                        saving.getBalance().decreaseAmount(amount.add(saving.getPenaltyFee()) );
                        saving.setPenalty(true);
                        LOGGER.info("Amount debited = " + amount.add(saving.getPenaltyFee()));


                    } else {
                        saving.getBalance().decreaseAmount(amount);
                        LOGGER.info("Amount debited = " + amount);

                    }
                    savingRepository.save(saving);
                    LOGGER.info("New amount = " + saving.getBalance().getAmount());
                    transaction.setAccountSender(saving);
                    break;
                }

                else {
                    throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount() );
                }

            case "creditcard":
                LOGGER.info("Searching CreditCard Account");
                CreditCard creditCard = creditCardRepository.findById(originId).orElseThrow(()-> new WrongInput("There is not "+ originType +" with id " + originId));
                creditCard.check();
                LOGGER.info("CreditCard Account Found with id " + originId );

                if (  (creditCard.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0 ) {

                    creditCard.getBalance().decreaseAmount(amount);
                    creditCardRepository.save(creditCard);
                    transaction.setAccountSender(creditCard);

                    LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                    break;
                } else {
                    throw new NotEnoughFunds("There is not enough funds. Actual balance: " + creditCard.getBalance().getAmount() );

                }

            case "checking":
                LOGGER.info("Searching Checking Account");
                Checking checking = checkingReposiroty.findById(originId).orElseThrow(()-> new WrongInput("There is not "+ originType +" with id " + originId));
                LOGGER.info("Checking Account Found with id " + originId  );

                if ( ( (checking.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0)  ) {

                    if  ( (!checking.isPenalty()) && ( (checking.getBalance().getAmount().subtract(amount)).compareTo(checking.getMinimumBalance())  <= 0 )  ){
                        checking.getBalance().decreaseAmount(amount.add(checking.getPenaltyFee()) );
                        checking.setPenalty(true);
                        LOGGER.info("Amount debited = " + amount.add(checking.getPenaltyFee()));

                    } else {
                        checking.getBalance().decreaseAmount(amount);

                    }
                    checkingReposiroty.save(checking);
                    LOGGER.info("New amount = " + checking.getBalance().getAmount());
                    transaction.setAccountSender(checking);
                    break;

                }
                else {
                    throw new NotEnoughFunds("There is not enough funds. Actual balance: " + checking.getBalance().getAmount() );
                }

            case "student":
                LOGGER.info("Searching Student Account");
                StudentChecking studentChecking = studentCheckingRepository.findById(originId).orElseThrow(()-> new WrongInput("There is not "+ originType +" with id " + originId));
                LOGGER.info("Student Account Found with id " + originId );

                if( ( (studentChecking.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0) ) {

                    studentChecking.getBalance().decreaseAmount(amount);
                    studentCheckingRepository.save(studentChecking);
                    LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                    transaction.setAccountSender(studentChecking);
                    break;
                } else {
                    throw new NotEnoughFunds("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount() );

                }


        }

        switch (destineType) {
            case "saving":
                LOGGER.info("Searching Saving Account");
                Saving saving = savingRepository.findById(destineId).orElseThrow(()-> new  WrongInput("There is not "+ destineType +" with id " + destineId));
                saving.check();
                LOGGER.info("Saving Account Found with id " + destineId);

                if ( (receptorName.equals(saving.getPrimaryOwner().getName())) || (receptorName.equals(saving.getSecondaryOwner().getName())) ) {
                    if ((saving.isPenalty()) && ((saving.getBalance().getAmount().add(amount)).compareTo(saving.getMinimumBalance()) >= 0)) {
                        saving.getBalance().increaseAmount(amount);
                        saving.setPenalty(false);
                        LOGGER.info("Added = " + amount);
                        LOGGER.info("New amount = " + saving.getBalance().getAmount());
                    } else {
                        saving.getBalance().increaseAmount(amount);
                        LOGGER.info("New amount = " + saving.getBalance().getAmount());
                    }
                    savingRepository.save(saving);
                    transaction.setAccountReceptor(saving);
                    break;
                } else {
                    throw new WrongInput("Receptor name was not found in account type" + destineType);
                }

            case "creditcard":
                LOGGER.info("Searching CreditCard Account");
                CreditCard creditCard = creditCardRepository.findById(destineId).orElseThrow(()-> new  WrongInput("There is not "+ destineType +" with id " + destineId));
                creditCard.check();
                LOGGER.info("CreditCard Account Found with id " + destineId);
                if ( (receptorName.equals(creditCard.getPrimaryOwner().getName())) || (receptorName.equals(creditCard.getSecondaryOwner().getName())) ) {

                    creditCard.getBalance().increaseAmount(amount);
                    creditCardRepository.save(creditCard);
                    LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                    transaction.setAccountReceptor(creditCard);
                    break;
                }else {
                    throw new WrongInput("Receptor name was not found in account type" + destineType);
                }
            case "checking":
                LOGGER.info("Searching Checking Account");
                Checking checking = checkingReposiroty.findById(destineId).orElseThrow(()-> new  WrongInput("There is not "+ destineType +" with id " + destineId));
                LOGGER.info("Checking Account Found");
                if ( (receptorName.equals(checking.getPrimaryOwner().getName())) || (receptorName.equals(checking.getSecondaryOwner().getName())) ) {

                    if ((checking.isPenalty()) && ((checking.getBalance().getAmount().add(amount)).compareTo(checking.getMinimumBalance()) >= 0)) {
                        checking.getBalance().increaseAmount(amount);
                        checking.setPenalty(false);
                        LOGGER.info("Added = " + amount);
                        LOGGER.info("New amount = " + checking.getBalance().getAmount());

                    } else {
                        checking.getBalance().increaseAmount(amount);
                        LOGGER.info("New amount = " + checking.getBalance().getAmount());
                    }
                    checkingReposiroty.save(checking);
                    transaction.setAccountReceptor(checking);
                    break;
                }else {
                    throw new WrongInput("Receptor name was not found in account type" + destineType);
                }
            case "student":
                LOGGER.info("Searching Student Account");
                StudentChecking studentChecking = studentCheckingRepository.findById(destineId).orElseThrow(()-> new  WrongInput("There is not "+ destineType +" with id " + destineId));
                LOGGER.info("Student Account Found");
                if ( (receptorName.equals(studentChecking.getPrimaryOwner().getName())) || (receptorName.equals(studentChecking.getSecondaryOwner().getName())) ) {

                    studentChecking.getBalance().increaseAmount(amount);
                    studentCheckingRepository.save(studentChecking);
                    LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                    transaction.setAccountReceptor(studentChecking);
                    break;
                }else {
                    throw new WrongInput("Receptor name was not found in account type" + destineType);
                }

        }
        transaction.setDateTransaction(LocalDateTime.now());
       return transactionRepository.save(transaction);

    }
}
