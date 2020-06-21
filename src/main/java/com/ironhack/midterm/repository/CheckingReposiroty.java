package com.ironhack.midterm.repository;

import com.ironhack.midterm.model.Checking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingReposiroty extends JpaRepository<Checking, Integer> {
}
