package com.poc.saccopoc.repositories;

import com.poc.saccopoc.entities.Account;
import com.poc.saccopoc.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountOrderByIdDesc(Account account);
}
