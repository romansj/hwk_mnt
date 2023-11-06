package com.jromans.hwk.fx.db;

import com.jromans.hwk.shared.constants.MintosCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByBaseAndOutputOrderByDateTimeDesc(MintosCurrency base, MintosCurrency output);

}
