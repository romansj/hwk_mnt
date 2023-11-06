package com.jromans.hwk.accounts;

import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.shared.constants.MintosCurrency;

import java.math.BigDecimal;
import java.util.List;

public interface IAccount {
    String getAccountNumber();

    MintosCurrency getAccountCurrency();

    List<Balance> getBalances();

    BigDecimal getBalanceInAccountCurrency();

    BigDecimal getBalanceInCurrency(MintosCurrency currency);
}
