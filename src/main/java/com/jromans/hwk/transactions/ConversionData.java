package com.jromans.hwk.transactions;

import com.jromans.hwk.shared.constants.MintosCurrency;

import java.math.BigDecimal;

record ConversionData(MintosCurrency accountCurrency, MintosCurrency requestCurrency, BigDecimal requestAmount, BigDecimal conversionAmount) {
}
