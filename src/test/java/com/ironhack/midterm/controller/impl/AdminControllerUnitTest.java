package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.dto.UserRequest;
import com.ironhack.midterm.model.users.*;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.RoleRepository;
import com.ironhack.midterm.repository.UserRepository;
import com.ironhack.midterm.service.AccountHolderService;
import com.ironhack.midterm.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@SpringBootTest
class AdminControllerUnitTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        ThirdParty thirdParty = new ThirdParty("pepito", "pepito");
        Role role = new Role("ROLE_THIRDPARTY", thirdParty);

        List<User> users = Collections.singletonList(thirdParty);

        when(userRepository.save(Mockito.any(ThirdParty.class))).thenAnswer(i -> i.getArguments()[0]);
        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.findAll()).thenReturn(users);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll() {
        List<User> users = adminService.findAll();
        assertEquals("pepito", users.get(0).getUsername());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void create() {
        UserRequest userRequest = new UserRequest("pepito", "pepito");
        ThirdParty thirdParty = adminService.create(userRequest);
        assertEquals( "pepito" , thirdParty.getUsername());
    }

}