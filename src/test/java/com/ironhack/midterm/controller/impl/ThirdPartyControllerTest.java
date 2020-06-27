package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.dto.TransactionThirdPartyRequest;
import com.ironhack.midterm.dto.UserRequest;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Address;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ThirdPartyControllerTest {
    @Autowired
    private AccountHolderRepository accountHolderRepository;

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
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        AccountUser accountUser = new AccountUser("pepito", passwordEncoder.encode("pepito"));
        Role role = new Role("ROLE_ACCOUNTHOLDER", accountUser);

        AccountHolder accountHolder = new AccountHolder(
                "Pepe", LocalDate.of(1999, 12, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);

        accountUserRepository.save(accountUser);
        roleRepository.save(role);
        accountHolderRepository.save(accountHolder);
        /** STUDENT **/

        StudentChecking studentChecking = new StudentChecking(new Money(new BigDecimal("1000")), "secret",
                accountHolder, null, new BigDecimal("40"), Status.ACTIVE);
        studentCheckingRepository.save(studentChecking);


        /** SAVING **/

        Saving saving = new Saving(new Money(new BigDecimal("1000")), "secret",
                accountHolder,null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("0.10"));
        savingRepository.save(saving);

        /** CREDITCARD **/

        CreditCard creditCard = new CreditCard(new Money(new BigDecimal("1000")),
                accountHolder,null, new BigDecimal("40"),
                new BigDecimal("1000"), new BigDecimal("0.10"));
        creditCardRepository.save(creditCard);

        /** CHECKING **/

        Checking checking = new Checking(new Money(new BigDecimal("1000")), "secret",
                accountHolder, null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("250"));
        checkingReposiroty.save(checking);
    }

    @AfterEach
    void tearDown(){
        checkingReposiroty.deleteAll();
        creditCardRepository.deleteAll();
        savingRepository.deleteAll();
        studentCheckingRepository.deleteAll();

        accountHolderRepository.deleteAll();

        accountUserRepository.deleteAll();

        roleRepository.deleteAll();

    }

    @Test
    void debit_saving() throws Exception {
        List<Saving> savings = savingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                savings.get(0).getId(), "secret");
        mockMvc.perform(post("/debit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
    @Test
    void debit_saving_badRequestt() throws Exception {
        List<Saving> savings = savingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                savings.get(0).getId(), "perro");
        mockMvc.perform(post("/debit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
    @Test
    void debit_checking() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                checkings.get(0).getId(), "secret");
        mockMvc.perform(post("/debit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
    @Test
    void debit_checking_badRequest() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                checkings.get(0).getId(), "perro");
        mockMvc.perform(post("/debit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
    @Test
    void debit_student() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "secret");
        mockMvc.perform(post("/debit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
    @Test
    void debit_student_badRequest() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "perro");
        mockMvc.perform(post("/debit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    @Test
    void credit_saving() throws Exception {
        List<Saving> savings = savingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                savings.get(0).getId(),"secret");
        mockMvc.perform(post("/credit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
    @Test
    void credit_saving_badRequest() throws Exception {
        List<Saving> savings = savingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                savings.get(0).getId(), "perro");
        mockMvc.perform(post("/credit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void credit_checking() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                checkings.get(0).getId(), "secret");
        mockMvc.perform(post("/credit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
    @Test
    void credit_checking_badRequest() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                checkings.get(0).getId(), "perro");
        mockMvc.perform(post("/credit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void credit_student() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "secret");
        mockMvc.perform(post("/credit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
    @Test
    void credit_student_badRequestt() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();
        TransactionThirdPartyRequest transactionThirdPartyRequest = new TransactionThirdPartyRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "perro");
        mockMvc.perform(post("/credit/thirdparty")
                .with(user("thirdparty").roles("THIRDPARTY"))
                .content(objectMapper.writeValueAsString(transactionThirdPartyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}