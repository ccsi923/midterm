package com.ironhack.midterm.repository;

import com.ironhack.midterm.model.Account;
import com.ironhack.midterm.model.users.AccountHolder;
import com.ironhack.midterm.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {

    public AccountHolder findByAccountUser(User user);
}
