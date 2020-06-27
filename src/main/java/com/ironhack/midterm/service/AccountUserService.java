package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.BalanceVM;
import com.ironhack.midterm.dto.TransactionAccountUserRequest;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.IllegalTransactionException;
import com.ironhack.midterm.exceptions.NotEnoughFunds;
import com.ironhack.midterm.exceptions.StatusFrozenException;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.AccountHolder;
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
import java.time.Duration;
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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;


    @Secured({"ROLE_ACCOUNTHOLDER"})
    public List<BalanceVM> findAllBalance(User user){
        LOGGER.info("[INIT] - findAllBalance");
        User userFound = accountUserRepository.findByUsername(user.getUsername());
        AccountHolder accountHolder = accountHolderRepository.findByAccountUser(userFound);
        List<Account> accounts= accountRepository.findAllById(accountHolder.getId());
        List<BalanceVM> balanceVMS = new ArrayList<>();
        for (Account account : accounts) {
            balanceVMS.add(new BalanceVM(account.getId(),account.getPrimaryOwner().getName(),account.getBalance().getAmount()));
        }
        return balanceVMS;
    }

    @Secured({"ROLE_ACCOUNTHOLDER"})
    public BalanceVM findBalanceByUserAndId(User user, Integer accountId){
        LOGGER.info("[INIT] - findBalanceByUserAndId");
        User userFound = accountUserRepository.findByUsername(user.getUsername());
        AccountHolder accountHolder = accountHolderRepository.findByAccountUser(userFound);
        Account account = accountRepository.findAccountById(accountHolder.getId() , accountId);

        BalanceVM balanceVMS;
        if(account instanceof Checking){
            ((Checking) account).check();
            LOGGER.info("Account Checking has been found and checked");
            balanceVMS = new BalanceVM(account.getId(),account.getPrimaryOwner().getName(),account.getBalance().getAmount());
            checkingReposiroty.save(((Checking) account));
            LOGGER.info("[END] - findBalanceByUserAndId");
            return balanceVMS;
        }
        if(account instanceof StudentChecking){
            LOGGER.info("Account Student has been found");
            balanceVMS = new BalanceVM(account.getId(),account.getPrimaryOwner().getName(),account.getBalance().getAmount());
            studentCheckingRepository.save(((StudentChecking) account));
            LOGGER.info("[END] - findBalanceByUserAndId");
            return balanceVMS;
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            LOGGER.info("Account Saving has been found and checked");
            balanceVMS = new BalanceVM(account.getId(),account.getPrimaryOwner().getName(),account.getBalance().getAmount());
            savingRepository.save(((Saving) account));
            LOGGER.info("[END] - findBalanceByUserAndId");
            return balanceVMS;
        }
        if(account instanceof CreditCard){
            ((CreditCard) account).check();
            LOGGER.info("Account CreditCard has been found and checked");
            balanceVMS = new BalanceVM(account.getId(),account.getPrimaryOwner().getName(),account.getBalance().getAmount());
            creditCardRepository.save(((CreditCard) account));
            LOGGER.info("[END] - findBalanceByUserAndId");
            return balanceVMS;
        }
        throw new WrongInput("Not Found");
    }

    @Secured({"ROLE_ACCOUNTHOLDER"})
    @Transactional(dontRollbackOn = StatusFrozenException.class )
    public Transaction transactions(User user, TransactionAccountUserRequest transactionAccountUserRequest){
        LOGGER.info("[INIT] - transactions");

        String originType = transactionAccountUserRequest.getTypeOriginAccount().toLowerCase().trim();
        String destineType = transactionAccountUserRequest.getTypeDestineAccount().toLowerCase().trim();
        Integer originId = transactionAccountUserRequest.getOwnAccountId();
        Integer destineId = transactionAccountUserRequest.getForeignAccountId();
        BigDecimal amount = transactionAccountUserRequest.getAmount();
        String receptorName = transactionAccountUserRequest.getReceptorName();

        Account accountFound = accountRepository.findById(originId).orElseThrow(()-> new WrongInput("Account with id "+ originId + " not fund" ));
        Transaction transaction = new Transaction();
        if(!user.getUsername().equals(accountFound.getPrimaryOwner().getAccountUser().getUsername())){
            if (accountFound.getSecondaryOwner() == null || !user.getUsername().equals(accountFound.getSecondaryOwner().getAccountUser().getUsername())) {
                LOGGER.error("User" + user.getUsername() + " is not the owner of the account");
                throw new IllegalTransactionException("User " + user.getUsername() + " is not the owner of the account");
            }
        }

            /** Checking Frozen**/
            boolean frozen = fraudDetection(transactionAccountUserRequest);

            switch (originType) {
                case "saving":
                    LOGGER.info("Searching Saving Account");
                    Saving saving = savingRepository.findById(originId).orElseThrow(() -> new WrongInput("There is not " + originType + " with id " + originId));
                    if (frozen) {
                        saving.setStatus(Status.FROZEN);
                        LOGGER.info(originType + " account with id " + originId + " has been frozen");
                        throw new StatusFrozenException(originType + " account with id " + originId + "has been frozen");
                    }
                    if (saving.getStatus().equals(Status.FROZEN)) {
                        LOGGER.error(originType + " account with id " + originId + "is frozen");
                        throw new StatusFrozenException(originType + " account with id " + originId + "is frozen");
                    } else {
                        saving.check();
                        LOGGER.info("Saving Account Found with id " + originId);
                        if (((saving.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0)) {

                            if ((!saving.isPenalty())
                                    &&
                                    ((saving.getBalance().getAmount().subtract(amount)).compareTo(saving.getMinimumBalance()) <= 0)) {
                                saving.getBalance().decreaseAmount(amount.add(saving.getPenaltyFee()));
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
                        } else {
                            LOGGER.error("There is not enough funds. Actual balance: " + saving.getBalance().getAmount());
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + saving.getBalance().getAmount());
                        }
                    }
                case "creditcard":
                    LOGGER.info("Searching CreditCard Account");
                    CreditCard creditCard = creditCardRepository.findById(originId).orElseThrow(() -> new WrongInput("There is not " + originType + " with id " + originId));
                    creditCard.check();
                    LOGGER.info("CreditCard Account Found with id " + originId);

                    if ((creditCard.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0) {

                        creditCard.getBalance().decreaseAmount(amount);
                        creditCardRepository.save(creditCard);
                        transaction.setAccountSender(creditCard);

                        LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                        break;
                    } else {
                        LOGGER.error("There is not enough funds. Actual balance: " + creditCard.getBalance().getAmount());
                        throw new NotEnoughFunds("There is not enough funds. Actual balance: " + creditCard.getBalance().getAmount());

                    }

                case "checking":
                    LOGGER.info("Searching Checking Account");
                    Checking checking = checkingReposiroty.findById(originId).orElseThrow(() -> new WrongInput("There is not " + originType + " with id " + originId));
                    checking.check();
                    LOGGER.info("Checking Account Found with id " + originId);
                    if (frozen) {
                        checking.setStatus(Status.FROZEN);
                        LOGGER.info(originType + " account with id " + originId + "has been frozen");
                        throw new StatusFrozenException(originType + " account with id " + originId + "has been frozen");
                    }
                    if (checking.getStatus().equals(Status.FROZEN)) {
                        LOGGER.error(originType + " account with id " + originId + "is frozen");
                        throw new StatusFrozenException(originType + " account with id " + originId + "is frozen");
                    } else {
                        if (((checking.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0)) {

                            if ((!checking.isPenalty()) && ((checking.getBalance().getAmount().subtract(amount)).compareTo(checking.getMinimumBalance()) <= 0)) {
                                checking.getBalance().decreaseAmount(amount.add(checking.getPenaltyFee()));
                                checking.setPenalty(true);
                                LOGGER.info("Amount debited = " + amount.add(checking.getPenaltyFee()));

                            } else {
                                checking.getBalance().decreaseAmount(amount);

                            }
                            checkingReposiroty.save(checking);
                            LOGGER.info("New amount = " + checking.getBalance().getAmount());
                            transaction.setAccountSender(checking);
                            break;

                        } else {
                            LOGGER.error("There is not enough funds. Actual balance: " + checking.getBalance().getAmount());
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + checking.getBalance().getAmount());
                        }
                    }

                case "student":
                    LOGGER.info("Searching Student Account");
                    StudentChecking studentChecking = studentCheckingRepository.findById(originId).orElseThrow(() -> new WrongInput("There is not " + originType + " with id " + originId));
                    LOGGER.info("Student Account Found with id " + originId);
                    if (frozen) {
                        studentChecking.setStatus(Status.FROZEN);
                        LOGGER.info(originType + " account with id " + originId + "has been frozen");
                        throw new StatusFrozenException(originType + " account with id " + originId + " has been frozen");
                    }
                    if (studentChecking.getStatus().equals(Status.FROZEN)) {
                        LOGGER.error(originType + " account with id " + originId + "is frozen");
                        throw new StatusFrozenException(originType + " account with id " + originId + " is frozen");
                    } else {
                        if (((studentChecking.getBalance().getAmount().subtract(amount)).compareTo(new BigDecimal("0")) >= 0)) {

                            studentChecking.getBalance().decreaseAmount(amount);
                            studentCheckingRepository.save(studentChecking);
                            LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                            transaction.setAccountSender(studentChecking);
                            break;
                        } else {
                            LOGGER.error("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount());
                            throw new NotEnoughFunds("There is not enough funds. Actual balance: " + studentChecking.getBalance().getAmount());

                        }
                    }
            }
            switch (destineType) {
                case "saving":
                    LOGGER.info("Searching Saving Account");
                    Saving saving = savingRepository.findById(destineId).orElseThrow(() -> new WrongInput("There is not " + destineType + " with id " + destineId));
                    saving.check();
                    LOGGER.info("Saving Account Found with id " + destineId);

                    if ((receptorName.equals(saving.getPrimaryOwner().getName())) || (receptorName.equals(saving.getSecondaryOwner().getName()))) {
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
                    CreditCard creditCard = creditCardRepository.findById(destineId).orElseThrow(() -> new WrongInput("There is not " + destineType + " with id " + destineId));
                    creditCard.check();
                    LOGGER.info("CreditCard Account Found with id " + destineId);
                    if ((receptorName.equals(creditCard.getPrimaryOwner().getName())) || (receptorName.equals(creditCard.getSecondaryOwner().getName()))) {

                        creditCard.getBalance().increaseAmount(amount);
                        creditCardRepository.save(creditCard);
                        LOGGER.info("New amount = " + creditCard.getBalance().getAmount());
                        transaction.setAccountReceptor(creditCard);
                        break;
                    } else {
                        throw new WrongInput("Receptor name was not found in account type" + destineType);
                    }
                case "checking":
                    LOGGER.info("Searching Checking Account");
                    Checking checking = checkingReposiroty.findById(destineId).orElseThrow(() -> new WrongInput("There is not " + destineType + " with id " + destineId));
                    checking.check();
                    LOGGER.info("Checking Account Found");
                    if ((receptorName.equals(checking.getPrimaryOwner().getName())) || (receptorName.equals(checking.getSecondaryOwner().getName()))) {

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
                    } else {
                        throw new WrongInput("Receptor name was not found in account type" + destineType);
                    }
                case "student":
                    LOGGER.info("Searching Student Account");
                    StudentChecking studentChecking = studentCheckingRepository.findById(destineId).orElseThrow(() -> new WrongInput("There is not " + destineType + " with id " + destineId));
                    LOGGER.info("Student Account Found");
                    if ((receptorName.equals(studentChecking.getPrimaryOwner().getName())) || (receptorName.equals(studentChecking.getSecondaryOwner().getName()))) {

                        studentChecking.getBalance().increaseAmount(amount);
                        studentCheckingRepository.save(studentChecking);
                        LOGGER.info("New amount = " + studentChecking.getBalance().getAmount());
                        transaction.setAccountReceptor(studentChecking);
                        break;
                    } else {
                        throw new WrongInput("Receptor name was not found in account type" + destineType);
                    }

            }
            transaction.setDateTransaction();
            transaction.setAmount(amount);
        LOGGER.info("[END] - transactions");
        return transactionRepository.save(transaction);

    }

    public boolean fraudDetection(TransactionAccountUserRequest transfer) {
        LOGGER.info("[INIT] - fraudDetection");
        boolean fraud = false;
        BigDecimal highestTransaction = transactionRepository.highestTransaction(LocalDateTime.now(),transfer.getOwnAccountId());
        LocalDateTime lastTransaction = transactionRepository.findByLastTransaction(transfer.getOwnAccountId());
        BigDecimal senderTransaction = transactionRepository.highestTransactionOwner(LocalDateTime.now(), transfer.getOwnAccountId());
        System.out.println(highestTransaction);
        System.out.println(senderTransaction);

        if(senderTransaction == null){
            senderTransaction = new BigDecimal("0");
        }
        if (  (lastTransaction != null) && (Duration.between(lastTransaction, LocalDateTime.now()).getSeconds() < 1) ) {
                LOGGER.error("Fraud detected: More than 2 transactions occurring within a 1 second");
                fraud = true;

        } else if ( (highestTransaction!=null) &&
            (highestTransaction.multiply(new BigDecimal("2.50")).compareTo(senderTransaction.add(transfer.getAmount())) < 0)){
                LOGGER.error("Transactions made in 24 hours that total to more than 150% of the customers highest daily total transactions in any other 24 hour period.");
                fraud = true;
        }
        LOGGER.info("[END] - fraudDetection");
        return fraud;
    }

}
