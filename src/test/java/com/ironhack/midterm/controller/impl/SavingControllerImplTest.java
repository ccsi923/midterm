package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.dto.AccountRequest;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.Saving;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SavingControllerImplTest {

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
                "Pepe", LocalDate.of(1989, 12, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);

        accountUserRepository.save(accountUser);
        roleRepository.save(role);
        accountHolderRepository.save(accountHolder);

        AccountUser accountUser2 = new AccountUser("anas", passwordEncoder.encode("ana"));
        Role role2 = new Role("ROLE_ACCOUNTHOLDER", accountUser2);
        AccountHolder accountHolder2 = new AccountHolder(
                "Pepe", LocalDate.of(1989, 11, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser2.setAccountHolder(accountHolder2);
        accountHolder2.setAccountUser(accountUser2);

        accountUserRepository.save(accountUser2);
        roleRepository.save(role2);
        accountHolderRepository.save(accountHolder2);

        /** SAVING **/

        Saving saving = new Saving(new Money(new BigDecimal("1000")), "secret",
                accountHolder,null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("0.10"));
        savingRepository.save(saving);
    }

    @AfterEach
    void tearDown(){
        savingRepository.deleteAll();

        accountHolderRepository.deleteAll();

        accountUserRepository.deleteAll();

        roleRepository.deleteAll();

    }

    @Test
    void findAll() throws Exception {
        MvcResult result =   mockMvc.perform(get("/savings")
                .with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));

    }
    @Test
    void create() throws Exception {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AccountUser accountUser = new AccountUser("jorge", passwordEncoder.encode("andrea"));

        AccountHolder accountHolder = new AccountHolder(
                "Pepe", LocalDate.of(1989, 12, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);

        AccountUser accountUser2 = new AccountUser("kika", passwordEncoder.encode("koko"));
        AccountHolder accountHolder2 = new AccountHolder(
                "Pepe", LocalDate.of(1989, 11, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser2.setAccountHolder(accountHolder2);
        accountHolder2.setAccountUser(accountUser2);

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount(new BigDecimal("1000"));
        accountRequest.setPrimaryOwner(accountHolder);
        accountRequest.setSecondaryOwner(accountHolder2);
        accountRequest.setSecretKey("perro");

        mockMvc.perform(post("/saving/")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void create_givenIdByParam() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount(new BigDecimal("1000"));
        accountRequest.setSecretKey("perro");

        mockMvc.perform(post("/saving/?primary="+accountHolders.get(0).getId()+"&secondary="+accountHolders.get(1).getId())
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void create_BadRequest() throws Exception {

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount(new BigDecimal("1000"));
        mockMvc.perform(post("/saving/?primary=1000&secondary=10000")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}