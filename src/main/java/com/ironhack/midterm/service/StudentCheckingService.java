package com.ironhack.midterm.service;

import com.ironhack.midterm.dto.CreditCardMV;
import com.ironhack.midterm.dto.StudentCheckingMV;
import com.ironhack.midterm.repository.StudentCheckingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentCheckingService {

    private static final Logger LOGGER = LogManager.getLogger(CreditCardService.class);


    @Autowired
    private StudentCheckingRepository studentCheckingRepository;


    public List<StudentCheckingMV> findAll(){
        LOGGER.info("[INIT] - findAll");
        List<StudentCheckingMV> studentCheckingMVS = studentCheckingRepository.findAll().stream().map(
                studentChecking -> new StudentCheckingMV(studentChecking.getId(),studentChecking.getBalance(),studentChecking.getSecretKey(), studentChecking.getPrimaryOwner(),
                        studentChecking.getSecondaryOwner(), studentChecking.getPenaltyFee(), studentChecking.getStatus())
        ).collect(Collectors.toList());
        LOGGER.info("[END] - findAll");
        return studentCheckingMVS;
    }
}
