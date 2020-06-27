package com.ironhack.midterm.repository;

import com.ironhack.midterm.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {


    @Query("SELECT a FROM Account a WHERE primary_owner_id =:id OR secondary_owner_id =:id")
    public List<Account> findAllById(@Param("id") Integer userId);

    @Query("SELECT a FROM Account a WHERE (primary_owner_id =:owner OR secondary_owner_id =:owner) AND a.id =:idAc")
    public Account findAccountById(@Param("owner") Integer ownerId,@Param("idAc") Integer accountId);


}
