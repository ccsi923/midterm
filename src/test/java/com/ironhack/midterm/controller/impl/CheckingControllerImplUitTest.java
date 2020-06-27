package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.CheckingVM;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Checking;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.users.*;
import com.ironhack.midterm.repository.*;
import com.ironhack.midterm.service.AdminService;
import com.ironhack.midterm.service.CheckingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckingControllerImplUitTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private CheckingReposiroty checkingReposiroty;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CheckingService checkingService;

    @BeforeEach
    void setUp() {

        AccountUser accountUser = new AccountUser("pepito","pepito");
        AccountHolder accountHolder = new AccountHolder(
                "Pepe", LocalDate.of(1989, 12, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);

        /** CHECKING **/

        Checking checking = new Checking(new Money(new BigDecimal("1000")), "secret",
                accountHolder, null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("250"));
        List<Checking> checkings = Collections.singletonList(checking);
        //when(userRepository.save(Mockito.any(ThirdParty.class))).thenAnswer(i -> i.getArguments()[0]);
        //when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        when(checkingReposiroty.findAll()).thenReturn(checkings);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll() {
        List<CheckingVM> checkingVMS = checkingService.findAll();
        assertEquals(Status.ACTIVE, checkingVMS.get(0).getStatus());
    }
}