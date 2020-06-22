package com.ironhack.midterm.repository;


import com.ironhack.midterm.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Integer> {
}
