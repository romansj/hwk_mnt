package com.jromans.hwk.accounts.db;

import com.jromans.hwk.shared.constants.MintosCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByAccount_AccountNumberIgnoreCaseAndCurrency(String accountNumber, MintosCurrency currency);


}
