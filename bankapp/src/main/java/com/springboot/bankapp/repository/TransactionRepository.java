package com.springboot.bankapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.bankapp.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findByAccountId(Long accountId);
}