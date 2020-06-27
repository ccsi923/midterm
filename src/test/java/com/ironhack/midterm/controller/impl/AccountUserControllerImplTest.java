package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.dto.TransactionAccountUserRequest;
import com.ironhack.midterm.dto.TransactionRequest;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.*;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Address;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.repository.*;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountUserControllerImplTest {

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
    private AccountUser accountUser;



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
                new Address("Madrid", "España", "Higueras 55", "28007"),
                new Address("Madrid", "España", "Higueras 51", "28047"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);

        accountUserRepository.save(accountUser);
        roleRepository.save(role);
        accountHolderRepository.save(accountHolder);

        AccountUser accountUser2 = new AccountUser("anas", passwordEncoder.encode("ana"));
        Role role2 = new Role("ROLE_ACCOUNTHOLDER", accountUser2);
        AccountHolder accountHolder2 = new AccountHolder(
                "Pepe", LocalDate.of(1999, 11, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser2.setAccountHolder(accountHolder2);
        accountHolder2.setAccountUser(accountUser2);


        accountUserRepository.save(accountUser2);
        roleRepository.save(role2);
        accountHolderRepository.save(accountHolder2);

        AccountUser accountUser3 = new AccountUser("pepote", passwordEncoder.encode("pepote"));
        Role role3 = new Role("ROLE_ACCOUNTHOLDER", accountUser3);
        AccountHolder accountHolder3 = new AccountHolder(
                "Pepe", LocalDate.of(1989, 11, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser3.setAccountHolder(accountHolder3);
        accountHolder3.setAccountUser(accountUser3);
        accountUserRepository.save(accountUser3);
        roleRepository.save(role3);
        accountHolderRepository.save(accountHolder3);

        AccountUser accountUser4 = new AccountUser("anasegarra", passwordEncoder.encode("anasegarra"));
        Role role4 = new Role("ROLE_ACCOUNTHOLDER", accountUser4);
        AccountHolder accountHolder4 = new AccountHolder(
                "Pepe", LocalDate.of(1989, 11, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"),
                 new Address("Madrid", "España", "Higueras 51", "28047"));
        accountUser4.setAccountHolder(accountHolder4);
        accountHolder4.setAccountUser(accountUser4);
        accountUserRepository.save(accountUser4);
        roleRepository.save(role4);
        accountHolderRepository.save(accountHolder4);

        /** STUDENT **/

        StudentChecking studentChecking = new StudentChecking(new Money(new BigDecimal("1000")), "secret",
                accountHolder, null, new BigDecimal("40"), Status.ACTIVE);


        StudentChecking studentChecking2 = new StudentChecking(new Money(new BigDecimal("1000")), "secret",
                accountHolder2, null, new BigDecimal("40"), Status.ACTIVE);

        studentCheckingRepository.save(studentChecking);
        studentCheckingRepository.save(studentChecking2);

        /** SAVING **/

        Saving saving = new Saving(new Money(new BigDecimal("1000")), "secret",
                accountHolder,null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("0.10"));

        Saving saving2 = new Saving(new Money(new BigDecimal("1000")), "secret",
                accountHolder2,null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("0.10"));

        savingRepository.save(saving);
        savingRepository.save(saving2);

        /** CREDITCARD **/

        CreditCard creditCard = new CreditCard(new Money(new BigDecimal("1000")),
                accountHolder,null, new BigDecimal("40"),
                new BigDecimal("1000"), new BigDecimal("0.10"));

        CreditCard creditCard2 = new CreditCard(new Money(new BigDecimal("1000")),
                accountHolder2,null, new BigDecimal("40"),
                new BigDecimal("1000"), new BigDecimal("0.10"));

        creditCardRepository.save(creditCard);
        creditCardRepository.save(creditCard2);

        /** CHECKING **/

        Checking checking = new Checking(new Money(new BigDecimal("1000")), "secret",
                accountHolder3, null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("250"));

        Checking checking2 = new Checking(new Money(new BigDecimal("1000")), "secret",
                accountHolder4, null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("250"));


        Checking checking3 = new Checking(new Money(new BigDecimal("1000")), "secret",
                accountHolder3, null, new BigDecimal("40"), Status.FROZEN,
                new BigDecimal("1000"), new BigDecimal("250"));

        checkingReposiroty.save(checking);
        checkingReposiroty.save(checking2);
        checkingReposiroty.save(checking3);

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
    void findAll_ok() throws Exception {
        MvcResult result =    mockMvc.perform(get("/account/user/balance")
                .with(httpBasic("pepito", "pepito"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));

    }

    @Test
    void findAll_forbidden() throws Exception {
        mockMvc.perform(get("/account/user/balance")
                .with(httpBasic("ana", "ana"))).andExpect(status().isUnauthorized());
    }

    @Test
    void transactions_students() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "student",
                studentCheckings.get(1).getId(), "student", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepito", "pepito"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
    @Test
    void transactions_students_NotEnoughFunds() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("2000"),
                studentCheckings.get(0).getId(), "student",
                studentCheckings.get(1).getId(), "student", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepito", "pepito"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void transactions_saving() throws Exception {
        List<Saving> savings = savingRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                savings.get(0).getId(), "saving",
                savings.get(1).getId(), "saving", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepito", "pepito"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    @Test
    void transactions_saving_NotEnoughFunds() throws Exception {
        List<Saving> savings = savingRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("2000"),
                savings.get(0).getId(), "saving",
                savings.get(1).getId(), "saving", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepito", "pepito"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transactions_creditcard() throws Exception {
        List<CreditCard> creditCards = creditCardRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                creditCards.get(0).getId(), "creditcard",
                creditCards.get(1).getId(), "creditcard", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepito", "pepito"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    void transactions_creditcard_NotEnoughFunds() throws Exception {
        List<CreditCard> creditCards = creditCardRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("2000"),
                creditCards.get(0).getId(), "creditcard",
                creditCards.get(1).getId(), "creditcard", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepito", "pepito"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void transactions_checking() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                checkings.get(0).getId(), "checking",
                checkings.get(1).getId(), "checking", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepote", "pepote"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    @Test
    void transactions_checking_NotEnoughFunds() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("2000"),
                checkings.get(0).getId(), "checking",
                checkings.get(1).getId(), "checking", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepote", "pepote"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transactions_Unauthorized() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "student",
                studentCheckings.get(1).getId(), "student", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("ana", "ana"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void transactions_Forbidden() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();

        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                studentCheckings.get(0).getId(), "student",
                studentCheckings.get(1).getId(), "student", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("anas", "ana"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }


    @Test
    void findBalanceByUserAndId_student_ok() throws Exception {
        List<StudentChecking> studentCheckings = studentCheckingRepository.findAll();

        MvcResult result =  mockMvc.perform(get("/account/user/balance/"+studentCheckings.get(0).getId())
                .with(httpBasic("pepito", "pepito"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));

    }
    @Test
    void findBalanceByUserAndId_saving_ok() throws Exception {
        List<Saving> savings = savingRepository.findAll();

        MvcResult result =  mockMvc.perform(get("/account/user/balance/"+savings.get(0).getId())
                .with(httpBasic("pepito", "pepito"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));

    }
    @Test
    void findBalanceByUserAndId_creditcard_ok() throws Exception {
        List<CreditCard> creditCards = creditCardRepository.findAll();
        MvcResult result =  mockMvc.perform(get("/account/user/balance/"+creditCards.get(0).getId())
                .with(httpBasic("pepito", "pepito"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));
    }
    @Test
    void findBalanceByUserAndId_checking_ok() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();

        MvcResult result =  mockMvc.perform(get("/account/user/balance/"+checkings.get(0).getId())
                .with(httpBasic("pepote", "pepote"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));

    }
    @Test
    void transactions_checking_frozen() throws Exception {
        List<Checking> checkings = checkingReposiroty.findAll();
        TransactionAccountUserRequest transactionRequest = new TransactionAccountUserRequest(new BigDecimal("100"),
                checkings.get(2).getId(), "checking",
                checkings.get(1).getId(), "checking", "Pepe");
        mockMvc.perform(post("/transaction")
                .with(httpBasic("pepote", "pepote"))
                .content(objectMapper.writeValueAsString(transactionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



}