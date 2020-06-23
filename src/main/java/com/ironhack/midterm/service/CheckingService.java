package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.dto.CheckingMV;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.exceptions.WrongInput;
import com.ironhack.midterm.model.Checking;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.StudentChecking;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckingService {

    private static final Logger LOGGER = LogManager.getLogger(CheckingService.class);


    @Autowired
    private CheckingReposiroty checkingReposiroty;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    public List<CheckingMV> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<CheckingMV> checkingMVS = checkingReposiroty.findAll().stream().map(
                checking -> new CheckingMV(checking.getId(),checking.getBalance(),
                        checking.getPrimaryOwner(), checking.getSecondaryOwner(), checking.getPenaltyFee(),
                        checking.getStatus(), checking.getMinimumBalance(), checking.getMonthlyMaintenanceFee())
        ).collect(Collectors.toList());
        LOGGER.info("[END] - findAll");
        return checkingMVS;
    }

    public CheckingMV create(Integer primaryId, Integer secondaryId, AccountRequest accountRequest) {

        LOGGER.info("[INIT] - create");

        if ((primaryId != -1)) {
            if (accountHolderRepository.findById(primaryId).isEmpty()) {
                LOGGER.error("There is not Account Holder with id " + primaryId);
                throw new WrongInput("There is not Account Holder with id " + primaryId);
            } else {
                accountRequest.setPrimaryOwner(accountHolderRepository.findById(primaryId).get());
            }
        }

        if (secondaryId != -1) {
            if ((accountHolderRepository.findById(secondaryId).isEmpty())) {
                LOGGER.error("There is not Account Holder with id " + secondaryId);
                throw new WrongInput("There is not Account Holder with id " + secondaryId);
            } else {
                accountRequest.setSecondaryOwner(accountHolderRepository.findById(secondaryId).get());
            }
        }
        if (primaryId == -1) {
            if (accountRequest.getPrimaryOwner() != null) {
                LOGGER.info("Searching user with name " + accountRequest.getUserNamePrimary());
                Optional<User> found = userRepo.findByUsername(accountRequest.getUserNamePrimary());

                if (found.isEmpty()) {
                    LOGGER.info("User with name " + accountRequest.getUserNamePrimary() + " not found");

                    accountHolderRepository.save(accountRequest.getPrimaryOwner());
                    LOGGER.info("User with name " + accountRequest.getUserNamePrimary() + " has been added");

                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    AccountUser newUser = new AccountUser(accountRequest.getUserNamePrimary(), passwordEncoder.encode(accountRequest.getPrimaryOwner().getPassword()));
                    userRepo.save(newUser);
                    LOGGER.info("User with name " + accountRequest.getUserNamePrimary() + " has been added");

                    Role role = new Role("ROLE_ACCOUNTHOLDER", newUser);
                    roleRepository.save(role);

                } else {
                    LOGGER.error("The name of user " + found.get().getUsername() + " already exists");
                    throw new WrongInput("The username " + accountRequest.getUserNamePrimary() + " already exist");
                }

            } else {
                LOGGER.error("You must give a Primary Account Holder");
                throw new WrongInput("You must give a Primary Account Holder");
            }
        }

        if (secondaryId == -1) {
            if (accountRequest.getSecondaryOwner() != null) {
                Optional<User> found = userRepo.findByUsername(accountRequest.getUserNameSecondary());
                if (found.isEmpty()) {
                    accountHolderRepository.save(accountRequest.getSecondaryOwner());
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    AccountUser newUser = new AccountUser(accountRequest.getUserNameSecondary(), passwordEncoder.encode(accountRequest.getSecondaryOwner().getPassword()));
                    userRepo.save(newUser);
                    Role role = new Role("ROLE_ACCOUNTHOLDER", newUser);
                    roleRepository.save(role);

                } else {
                    throw new WrongInput("The username " + accountRequest.getUserNameSecondary() + " already exist");
                }
            }
        }

        if (Period.between(accountRequest.getPrimaryOwner().getBirth(), LocalDate.now()).getYears() < 24) {
            StudentChecking studentChecking = new StudentChecking(new Money(accountRequest.getAmount()), accountRequest.getSecretKey(),
                    accountRequest.getPrimaryOwner(), accountRequest.getSecondaryOwner(), new BigDecimal("40"), Status.ACTIVE);
            LOGGER.info("Saving -> account Checking Account");
            studentCheckingRepository.save(studentChecking);
            LOGGER.info("[END] - create");
            return new CheckingMV(studentChecking.getId(), studentChecking.getBalance(), studentChecking.getPrimaryOwner(), studentChecking.getSecondaryOwner(),
                    studentChecking.getPenaltyFee(), studentChecking.getStatus(), null, null);
        } else {

            if(accountRequest.getAmount().compareTo(new BigDecimal("250")) < 0){
                throw new WrongInput("Amount to create account must be greater than 250");
            } else {

            Checking checking = new Checking(new Money(accountRequest.getAmount()), accountRequest.getSecretKey(), accountRequest.getPrimaryOwner(),
                    accountRequest.getSecondaryOwner(), new BigDecimal("40"), Status.ACTIVE,
                    new BigDecimal("250"), new BigDecimal("12"));
            LOGGER.info("Saving -> account Checking Account");
            checkingReposiroty.save(checking);
            LOGGER.info("[END] - create");
            return new CheckingMV(checking.getId(), checking.getBalance(), checking.getPrimaryOwner(), checking.getSecondaryOwner(),
                    checking.getPenaltyFee(), checking.getStatus(), checking.getMinimumBalance(), checking.getMonthlyMaintenanceFee());
           }
      }
    }
}
