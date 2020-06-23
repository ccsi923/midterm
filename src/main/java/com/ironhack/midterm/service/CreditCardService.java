package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CreditCardMV;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.CreditCard;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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

    public List<CreditCardMV> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<CreditCardMV> creditCardMVS = creditCardRepository.findAll().stream().map(
                creditCard -> new CreditCardMV(creditCard.getId(),creditCard.getBalance(), creditCard.getPrimaryOwner(), creditCard.getSecondaryOwner(),
                        creditCard.getCreditLimit(), creditCard.getInterestRate(), creditCard.getPenaltyFee())
        ).collect(Collectors.toList());
        LOGGER.info("[END] - findAll");
        return creditCardMVS;
    }

    public CreditCardMV create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest){

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
        if (primaryId == -1){
            if (accountRequest.getPrimaryOwner() != null) {
                accountHolderRepository.save(accountRequest.getPrimaryOwner());
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                AccountUser newUser = new AccountUser(accountRequest.getPrimaryOwner().getName(),passwordEncoder.encode(accountRequest.getPrimaryOwner().getPassword()));
                userRepo.save(newUser);
                Role role = new Role("ROLE_ACCOUNTHOLDER",newUser);
                roleRepository.save(role);
            } else {
                LOGGER.error("You must give a Parimary Account Holder" );
                throw new WrongInput("You must give a Parimary Account Holder");
            }
        }

        if(secondaryId == -1){
            if (accountRequest.getSecondaryOwner() != null){
                accountHolderRepository.save(accountRequest.getSecondaryOwner());
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                AccountUser newUser = new AccountUser(accountRequest.getSecondaryOwner().getName(),passwordEncoder.encode(accountRequest.getSecondaryOwner().getPassword()));
                userRepo.save(newUser);
                Role role = new Role("ROLE_ACCOUNTHOLDER",newUser);
                roleRepository.save(role);
            }
        }

        if (accountRequest.getCreditLimit() == null){
            LOGGER.debug("Credit Limit -> 100");
            accountRequest.setCreditLimit(new BigDecimal("100"));
        }
        if ( (accountRequest.getCreditLimit().compareTo(new BigDecimal("100")) < 0 ) || (accountRequest.getCreditLimit().compareTo(new BigDecimal("100000")) > 0 )){
            LOGGER.error("Credit Limit must be between 100-100000");
            throw new WrongInput("Interest Rate must be under 0.5");
        }
        if (accountRequest.getInterestRate() == null){
            LOGGER.debug("Minimum Balance -> 1000");
            accountRequest.setInterestRate(new BigDecimal("0.2"));
        }
        if( (accountRequest.getInterestRate().compareTo(new BigDecimal("0.1")) < 0) || (accountRequest.getInterestRate().compareTo(new BigDecimal("0.2")) > 0) ){
            LOGGER.error("Interest Rate can't be under 0.1");
            throw new WrongInput("Interest Rate can't be under 0.1");
        }

        CreditCard creditCard = new CreditCard(new Money(accountRequest.getAmount()), accountRequest.getPrimaryOwner(),
                accountRequest.getSecondaryOwner(), accountRequest.getCreditLimit(), accountRequest.getInterestRate(),new BigDecimal("40"));
        LOGGER.info("Saving -> account Credit Card");
        creditCardRepository.save(creditCard);
        LOGGER.info("[END] - create");
        return new CreditCardMV(creditCard.getId(), creditCard.getBalance(), creditCard.getPrimaryOwner(), creditCard.getSecondaryOwner(),
                creditCard.getCreditLimit(), creditCard.getInterestRate(), creditCard.getPenaltyFee());
    }
}
