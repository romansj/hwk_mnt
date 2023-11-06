package com.jromans.hwk.fx;

import com.jromans.hwk.shared.constants.MintosCurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RateData(MintosCurrency base, MintosCurrency output, BigDecimal rate, LocalDateTime dateTime) {
}
