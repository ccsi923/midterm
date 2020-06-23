package com.ironhack.midterm.service;

import com.ironhack.midterm.model.users.User;
import com.ironhack.midterm.repository.RoleRepository;
import com.ironhack.midterm.repository.UserRepository;
import com.ironhack.midterm.security.CustomSecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("Invalid username/password combination.");

        return new CustomSecurityUser(user);    }






}
