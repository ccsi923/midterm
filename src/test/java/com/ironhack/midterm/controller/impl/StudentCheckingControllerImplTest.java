package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.StudentChecking;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StudentCheckingControllerImplTest {
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountUserRepository  accountUserRepository;


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
    }
    @AfterEach
    void tearDown(){
        studentCheckingRepository.deleteAll();

        accountHolderRepository.deleteAll();

        accountUserRepository.deleteAll();

        roleRepository.deleteAll();

    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/students")
                .with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pepe"));

    }
}