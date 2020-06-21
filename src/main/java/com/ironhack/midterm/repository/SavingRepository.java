package com.ironhack.midterm.repository;

import com.ironhack.midterm.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Integer> {
}
