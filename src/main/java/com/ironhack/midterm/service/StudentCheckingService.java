package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.StudentCheckingVM;
import com.ironhack.midterm.repository.RoleRepository;
import com.ironhack.midterm.repository.StudentCheckingRepository;
import com.ironhack.midterm.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentCheckingService {

    private static final Logger LOGGER = LogManager.getLogger(CreditCardService.class);

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Secured({"ROLE_ADMIN"})
    public List<StudentCheckingVM> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<StudentCheckingVM> studentCheckingVMS = new ArrayList<>();

        studentCheckingRepository.findAll().forEach(
                studentChecking -> {
                  studentCheckingVMS.add(new StudentCheckingVM(studentChecking.getId(),studentChecking.getBalance(),studentChecking.getSecretKey(),
                            studentChecking.getPrimaryOwner(), studentChecking.getSecondaryOwner(),
                            studentChecking.getPenaltyFee(), studentChecking.getStatus()));
                }
        );
        LOGGER.info("[END] - findAll");
        return studentCheckingVMS;
    }
}
