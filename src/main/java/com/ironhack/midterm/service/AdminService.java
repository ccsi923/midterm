package com.ironhack.midterm.service;


import com.ironhack.midterm.dto.*;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.NotEnoughFunds;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.AccountHolder;
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

    @Autowired
    private AccountRepository accountRepository;


    @Secured({"ROLE_ADMIN"})
    public ThirdParty create(UserRequest userRequest){
        LOGGER.info("[INIT] - create");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ThirdParty newUser = new ThirdParty(userRequest.getUserName(),passwordEncoder.encode(userRequest.getPassword()));
        userRepo.save(newUser);
        Role role = new Role("ROLE_THIRDPARTY", newUser);
        roleRepository.save(role);
        LOGGER.info("[END] - create");
        return newUser;

    }

    @Secured({"ROLE_ADMIN"})
    public AccountAdminAccess findById(Integer id){
        LOGGER.info("[INIT] - findById");

        Account account = accountRepository.findById(id).orElseThrow(()-> new WrongInput("Account with id"+ id +"not found"));

        if(account instanceof Checking){
            ((Checking) account).check();
            Checking checking = (Checking) account;
            LOGGER.info("Find Checking Account with Id " + id);
            LOGGER.info("[END] - findById");
            return  new AccountAdminAccess(checking.getId(), checking.getPrimaryOwner().getName(), checking.getBalance());

        }
        if(account instanceof StudentChecking){
            StudentChecking studentChecking = (StudentChecking) account;
            LOGGER.info("Find Student Account with Id " + id);
            LOGGER.info("[END] - findById");
            return  new AccountAdminAccess(studentChecking.getId(), studentChecking.getPrimaryOwner().getName(), studentChecking.getBalance());
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            Saving saving = (Saving) account;
            LOGGER.info("Find Saving Account with id " + id);
            LOGGER.info("[END] - findById");
            return  new AccountAdminAccess(saving.getId(), saving.getPrimaryOwner().getName(), saving.getBalance());
        }
        if(account instanceof CreditCard){
            ((CreditCard) account).check();
            CreditCard creditCard = (CreditCard) account;
            LOGGER.info("Find CreditCard Account with Id " + id);
            LOGGER.info("[END] - findById");
            return  new AccountAdminAccess(creditCard.getId(), creditCard.getPrimaryOwner().getName(), creditCard.getBalance());
        }
        throw new WrongInput("Not Found");
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void debit(TransactionRequest transactionRequest){
        LOGGER.info("[INIT] - debit");

        Account account = accountRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new WrongInput("Account with id "+ transactionRequest.getAccountId() +" not found"));

        if(account instanceof Checking){
            ((Checking) account).check();
            Checking checking = (Checking) account;
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
                LOGGER.error("There is not enough funds. Actual balance: " + checking.getBalance().getAmount());
                throw new NotEnoughFunds("There is not enough funds. Actual balance: " + checking.getBalance().getAmount() );
            }
        }
        if(account instanceof StudentChecking){
            StudentChecking studentChecking = (StudentChecking) account;

            if( ( (studentChecking.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0) ) {
                studentChecking.getBalance().decreaseAmount(transactionRequest.getAmount());
                studentCheckingRepository.save(studentChecking);
                LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                return;
            } else {
                LOGGER.error("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount());
                throw new NotEnoughFunds("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount() );
            }
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            Saving saving = (Saving) account;
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
                LOGGER.error("There is not enough funds. Actual balance: " + saving.getBalance().getAmount());
                throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount() );
            }

        }
        if(account instanceof CreditCard){
            ((CreditCard) account).check();
            CreditCard creditCard = (CreditCard) account;
            if (  (creditCard.getBalance().getAmount().subtract(transactionRequest.getAmount())).compareTo(new BigDecimal("0")) >= 0 ) {

                creditCard.getBalance().decreaseAmount(transactionRequest.getAmount());
                creditCardRepository.save(creditCard);
                LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                return;
            } else {
                LOGGER.error("There is not enough funds. Actual balance: " + creditCard.getBalance().getAmount());
                throw new NotEnoughFunds("There is not enough funds. Actual balance: " + creditCard.getBalance().getAmount() );
            }
        }
        throw new WrongInput("Not Found");
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void credit(TransactionRequest transactionRequest){
        LOGGER.info("[INIT] - credit");

        Account account = accountRepository.findById(transactionRequest.getAccountId()).orElseThrow(()-> new WrongInput("Account with id "+ transactionRequest.getAccountId() +" not found"));

        if(account instanceof Checking){
            ((Checking) account).check();
            Checking checking = (Checking) account;
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
            LOGGER.info("[INIT] - credit");
            return;
        }
        if(account instanceof StudentChecking){
            StudentChecking studentChecking = (StudentChecking) account;
            LOGGER.info("Student Account Found");
            studentChecking.setBalance( new Money (studentChecking.getBalance().increaseAmount(transactionRequest.getAmount())) );
            studentCheckingRepository.save(studentChecking);
            LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
            return;
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            Saving saving = (Saving) account;
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
        }
        if(account instanceof CreditCard){
            ((CreditCard) account).check();
            CreditCard creditCard = (CreditCard) account;
            LOGGER.info("CreditCard Account Found");
            creditCard.setBalance( new Money (creditCard.getBalance().increaseAmount(transactionRequest.getAmount())) );
            creditCardRepository.save(creditCard);
            LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
            return;
        }
        throw new WrongInput("Not Found");
    }

    @Secured({"ROLE_ADMIN"})
    public List<User> findAll(){
        return userRepo.findAll();
    }

    @Secured({"ROLE_ADMIN"})
    public void removeFrozen(Integer accountId){
        LOGGER.info("[INIT] - removeFrozen");
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new WrongInput("Account not found"));
        if(account instanceof Checking){
            ((Checking) account).check();
            LOGGER.info("Account Checking has been found and checked");
            if(((Checking) account).getStatus().equals(Status.ACTIVE)){
                LOGGER.error("Checking account status is already ACTIVE");
                throw new WrongInput("Checking account status is already ACTIVE");
            } else {
                ((Checking) account).setStatus(Status.ACTIVE);
                checkingReposiroty.save(((Checking) account));
                LOGGER.info("[END] - removeFrozen");
                return;
            }
        }
        if(account instanceof StudentChecking){
            LOGGER.info("Account Student has been found");
            if(((StudentChecking) account).getStatus().equals(Status.ACTIVE)){
                LOGGER.error("Student account status is already ACTIVE");
                throw new WrongInput("Student account status is already ACTIVE");
            } else {
                ((StudentChecking) account).setStatus(Status.ACTIVE);
                studentCheckingRepository.save(((StudentChecking) account));
                LOGGER.info("[END] - removeFrozen");
                return;
            }
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            if(((Saving) account).getStatus().equals(Status.ACTIVE)){
                LOGGER.error("Saving account status is already ACTIVE");
                throw new WrongInput("Saving account status is already ACTIVE");
            } else {
                LOGGER.info("Account Saving has been found and checked");
                ((Saving) account).setStatus(Status.ACTIVE);
                savingRepository.save(((Saving) account));
                LOGGER.info("[END] - removeFrozen");
                return;
            }
        }
        LOGGER.error("Fail update");
        throw new WrongInput("Fail update");
    }

}


