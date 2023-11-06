package com.jromans.hwk.accounts;

import com.jromans.hwk.shared.constants.MintosCurrency;

import java.math.BigDecimal;

public record BalanceDTO(Long id, String accountNumber, MintosCurrency currency, BigDecimal amount) {
}
