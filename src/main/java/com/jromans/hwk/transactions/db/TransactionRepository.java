package com.jromans.hwk.transactions.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountNumberIgnoreCase(String accountNumber);

}
