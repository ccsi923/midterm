package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CreditCardVM;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.CreditCard;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Role;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditCardService {

    private static final Logger LOGGER = LogManager.getLogger(CreditCardService.class);

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Secured({"ROLE_ADMIN"})
    public List<CreditCardVM> findAll() {
        LOGGER.info("[INIT] - findAll");
        List<CreditCardVM> creditCardVMS = new ArrayList<>();
                creditCardRepository.findAll().forEach(
                creditCard -> {
                    creditCardVMS.add(new CreditCardVM(creditCard.getId(), creditCard.getBalance(),
                            creditCard.getPrimaryOwner(), creditCard.getSecondaryOwner(),
                            creditCard.getCreditLimit(), creditCard.getInterestRate(), creditCard.getPenaltyFee()));
                });
        LOGGER.info("[END] - findAll");
        return creditCardVMS;
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public CreditCardVM create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest){

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
                    Role role = new Role("ROLE_ACCOUNTHOLDER", newUser);
                    roleRepository.save(role);
                    LOGGER.info("Created user with username " + accountRequest.getPrimaryOwner().getAccountUser().getUsername() + " and role ACCOUNTHOLDER");



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
                    LOGGER.info("User with name " + accountRequest.getPrimaryOwner().getAccountUser().getUsername() + " not found");

                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    AccountUser newUser = new AccountUser(accountRequest.getSecondaryOwner().getAccountUser().getUsername(),
                            passwordEncoder.encode(accountRequest.getSecondaryOwner().getAccountUser().getPassword()),
                            accountRequest.getSecondaryOwner());
                    accountRequest.getSecondaryOwner().setAccountUser(newUser);
                    userRepo.save(newUser);
                    accountHolderRepository.save(accountRequest.getSecondaryOwner());
                    Role role = new Role("ROLE_ACCOUNTHOLDER", newUser);
                    roleRepository.save(role);
                    LOGGER.info("Created user with username " +accountRequest.getSecondaryOwner().getAccountUser().getUsername() + " and role ACCOUNTHOLDER");


                } else {
                    LOGGER.error("The username " + accountRequest.getSecondaryOwner().getAccountUser().getUsername() + " already exist");
                    throw new WrongInput("The username " + accountRequest.getSecondaryOwner().getAccountUser().getUsername() + " already exist");
                }
            }
        }

        if (accountRequest.getCreditLimit() == null){
            LOGGER.info("Credit Limit -> 100");
            accountRequest.setCreditLimit(new BigDecimal("100"));
        }
        if ( (accountRequest.getCreditLimit().compareTo(new BigDecimal("100")) < 0 ) || (accountRequest.getCreditLimit().compareTo(new BigDecimal("100000")) > 0 )){
            LOGGER.error("Credit Limit must be between 100-100000");
            throw new WrongInput("Interest Rate must be under 0.5");
        }
        if (accountRequest.getInterestRate() == null){
            LOGGER.info("Minimum Balance -> 1000");
            accountRequest.setInterestRate(new BigDecimal("0.2"));
        }
        if( (accountRequest.getInterestRate().compareTo(new BigDecimal("0.1")) < 0) || (accountRequest.getInterestRate().compareTo(new BigDecimal("0.2")) > 0) ){
            LOGGER.error("Interest Rate can't be under 0.1");
            throw new WrongInput("Interest Rate can't be under 0.1");
        }

        CreditCard creditCard = new CreditCard(new Money(accountRequest.getAmount()), accountRequest.getPrimaryOwner(),
                accountRequest.getSecondaryOwner(),new BigDecimal("40"),accountRequest.getCreditLimit() , accountRequest.getInterestRate());

        creditCardRepository.save(creditCard);
        LOGGER.info("[END] - create");
        return new CreditCardVM(creditCard.getId(), creditCard.getBalance(), creditCard.getPrimaryOwner(), creditCard.getSecondaryOwner(),
                creditCard.getCreditLimit(), creditCard.getInterestRate(), creditCard.getPenaltyFee());
    }
}
