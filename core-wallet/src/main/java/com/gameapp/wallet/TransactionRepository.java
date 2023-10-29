package com.gameapp.wallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gameapp.wallet.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("select u from Transaction u where u.sid = ?1 OR u.rid = ?1")
    List<Transaction> findBysidAndrid(int sid);
}
