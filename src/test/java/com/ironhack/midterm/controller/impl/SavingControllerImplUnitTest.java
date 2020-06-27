package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.CreditCardVM;
import com.ironhack.midterm.dto.SavingVM;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.CreditCard;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.model.Saving;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.AccountUser;
import com.ironhack.midterm.model.users.Address;
import com.ironhack.midterm.repository.*;
import com.ironhack.midterm.service.CreditCardService;
import com.ironhack.midterm.service.SavingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class SavingControllerImplUnitTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private SavingRepository savingRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SavingService savingService;

    @BeforeEach
    void setUp() {

        AccountUser accountUser = new AccountUser("pepito","pepito");
        AccountHolder accountHolder = new AccountHolder(
                "Pepe", LocalDate.of(1989, 12, 12),
                new Address("Madrid", "España", "Higueras 55", "28007"));
        accountUser.setAccountHolder(accountHolder);
        accountHolder.setAccountUser(accountUser);

        /** CHECKING **/

        Saving saving = new Saving(new Money(new BigDecimal("1000")), "secret",
                accountHolder, null, new BigDecimal("40"), Status.ACTIVE,
                new BigDecimal("1000"), new BigDecimal("250"));
        List<Saving> savings = Collections.singletonList(saving);
        //when(userRepository.save(Mockito.any(ThirdParty.class))).thenAnswer(i -> i.getArguments()[0]);
        //when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        when(savingRepository.findAll()).thenReturn(savings);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll() {
        List<SavingVM> savingVMS = savingService.findAll();
        assertEquals("Pepe", savingVMS.get(0).getPrimaryOwner().getName());
    }
}