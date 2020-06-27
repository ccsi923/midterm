package com.ironhack.midterm.service;

import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.repository.RoleRepository;
import com.ironhack.midterm.repository.UserRepository;
import com.ironhack.midterm.security.CustomSecurityUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("[INIT] - loadUserByUsername");
        Optional<User> user = userRepo.findByUsername(username);

        if (user.isEmpty())
            throw new UsernameNotFoundException("Invalid username/password combination.");

        LOGGER.info("[END] - loadUserByUsername");
        return new CustomSecurityUser(user.get());    }






}
