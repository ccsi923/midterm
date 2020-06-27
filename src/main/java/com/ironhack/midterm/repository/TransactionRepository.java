package com.ironhack.midterm.repository;


import com.ironhack.midterm.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Integer> {

    @Query(value = "SELECT MAX(t.date_transaction) FROM transaction t WHERE sender_id =:id", nativeQuery = true)
    public LocalDateTime findByLastTransaction(@Param("id") Integer transId);


    @Query(value = "SELECT SUM(t.amount) FROM transaction t WHERE CAST(t.date_transaction AS DATE) !=:date && sender_id = :id GROUP BY CAST(t.date_transaction AS DATE), sender_id ORDER BY SUM(t.amount) DESC limit 1", nativeQuery = true)
    public BigDecimal highestTransaction(@Param("date") LocalDateTime localDateTime, @Param("id") Integer transId);//-->Here date of today

    @Query(value =  "SELECT SUM(t.amount) FROM transaction t  where CAST(t.date_transaction AS DATE) = CAST(:date AS DATE) AND sender_id=:id ORDER BY SUM(t.amount) DESC limit 1" ,nativeQuery = true)
    public BigDecimal highestTransactionOwner(@Param("date") LocalDateTime localDateTime, @Param("id") Integer transId);

}
