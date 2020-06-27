package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.AccountHolderVM;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Address;
import com.ironhack.midterm.model.users.Role;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.AccountUserRepository;
import com.ironhack.midterm.repository.RoleRepository;
import com.ironhack.midterm.service.AccountHolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class AccountHolderControllerImplUnitTest {

    @MockBean
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountHolderService accountHolderService;


    @BeforeEach
    void setUp() {
        AccountUser accountUser = new AccountUser("pepito", "pepito");
        AccountHolder accountHolder = new AccountHolder(
                "Pepe", LocalDate.of(1999, 12, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"),
                new Address("Madrid", "España", "Higueras 51", "28047"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);
        List<AccountHolder> accountHolders =  Collections.singletonList(accountHolder);

        when(accountHolderRepository.findAll()).thenReturn(accountHolders);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll() throws Exception {
        List<AccountHolderVM> accountHolders = accountHolderService.findAll();
        assertEquals("Pepe",accountHolders.get(0).getName());
    }
}