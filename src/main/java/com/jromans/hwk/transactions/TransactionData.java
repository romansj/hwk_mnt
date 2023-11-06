package com.jromans.hwk.transactions;

import com.jromans.hwk.shared.constants.MintosCurrency;
import com.jromans.hwk.shared.constants.TransactionType;

import java.math.BigDecimal;

public interface TransactionData {
    TransactionType getTransactionType();

    BigDecimal getAmount();

    MintosCurrency getCurrency();
}
