package com.jromans.hwk.accounts.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumberIgnoreCase(String accountNumber);

    List<Account> findByCustomer_CustId(Long custId);

}
