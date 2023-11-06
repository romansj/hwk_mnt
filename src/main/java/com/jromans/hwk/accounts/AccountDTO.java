package com.jromans.hwk.accounts;

import com.jromans.hwk.shared.constants.MintosCurrency;

import java.util.List;

public record AccountDTO(String accountNumber, List<BalanceDTO> balances, MintosCurrency accountCurrency, String customerId) {
}
