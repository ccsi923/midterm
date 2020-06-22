package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountDto;
import com.ironhack.midterm.dto.CreditCardMV;
import com.ironhack.midterm.dto.SavingMV;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.CreditCard;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.Saving;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.CheckingReposiroty;
import com.ironhack.midterm.repository.CreditCardRepository;
import com.ironhack.midterm.repository.SavingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<CreditCardMV> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<CreditCardMV> creditCardMVS = creditCardRepository.findAll().stream().map(
                creditCard -> new CreditCardMV(creditCard.getId(),creditCard.getBalance(), creditCard.getPrimaryOwner(), creditCard.getSecondaryOwner(),
                        creditCard.getCreditLimit(), creditCard.getInterestRate(), creditCard.getPenaltyFee())
        ).collect(Collectors.toList());
        LOGGER.info("[END] - findAll");
        return creditCardMVS;
    }

    public CreditCardMV create(Integer primaryId, Integer secondaryId, AccountDto accountDto){

        LOGGER.info("[INIT] - create");

        if((primaryId != -1)){
            if (accountHolderRepository.findById(primaryId).isEmpty()){
                LOGGER.error("There is not Account Holder with id " + primaryId);
                throw new WrongInput("There is not Account Holder with id " + primaryId );
            } else {
                accountDto.setPrimaryOwner(accountHolderRepository.findById(primaryId).get());
            }
        }

        if(secondaryId != -1){
            if((accountHolderRepository.findById(secondaryId).isEmpty())) {
                LOGGER.error("There is not Account Holder with id " + secondaryId);
                throw new WrongInput("There is not Account Holder with id " + secondaryId);
            } else {
                accountDto.setSecondaryOwner(accountHolderRepository.findById(secondaryId).get());
            }
        }
        if (primaryId == -1){
            if (accountDto.getPrimaryOwner() != null) {
                accountHolderRepository.save(accountDto.getPrimaryOwner());
            } else {
                LOGGER.error("You must give a Parimary Account Holder" );
                throw new WrongInput("You must give a Parimary Account Holder");
            }
        }

        if(secondaryId == -1){
            if (accountDto.getSecondaryOwner() != null){
                accountHolderRepository.save(accountDto.getPrimaryOwner());
            }
        }

        if (accountDto.getCreditLimit() == null){
            LOGGER.debug("Credit Limit -> 100");
            accountDto.setCreditLimit(new BigDecimal("100"));
        }
        if ( (accountDto.getCreditLimit().compareTo(new BigDecimal("100")) < 0 ) || (accountDto.getCreditLimit().compareTo(new BigDecimal("100000")) > 0 )){
            LOGGER.error("Credit Limit must be between 100-100000");
            throw new WrongInput("Interest Rate must be under 0.5");
        }
        if (accountDto.getInterestRate() == null){
            LOGGER.debug("Minimum Balance -> 1000");
            accountDto.setInterestRate(new BigDecimal("0.2"));
        }
        if( (accountDto.getInterestRate().compareTo(new BigDecimal("0.1")) < 0) || (accountDto.getInterestRate().compareTo(new BigDecimal("0.2")) > 0) ){
            LOGGER.error("Interest Rate can't be under 0.1");
            throw new WrongInput("Interest Rate can't be under 0.1");
        }

        CreditCard creditCard = new CreditCard(new Money(accountDto.getAmount()), accountDto.getPrimaryOwner(),
                accountDto.getSecondaryOwner(), accountDto.getCreditLimit(), accountDto.getInterestRate(),new BigDecimal("40"));
        LOGGER.info("Saving -> account Credit Card");
        creditCardRepository.save(creditCard);
        LOGGER.info("[END] - create");
        return new CreditCardMV(creditCard.getId(), creditCard.getBalance(), creditCard.getPrimaryOwner(), creditCard.getSecondaryOwner(),
                creditCard.getCreditLimit(), creditCard.getInterestRate(), creditCard.getPenaltyFee());
    }
}
