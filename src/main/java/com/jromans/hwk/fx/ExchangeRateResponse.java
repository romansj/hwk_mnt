package com.jromans.hwk.fx;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jromans.hwk.shared.constants.MintosCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ExchangeRateResponse(
        boolean success,
        long timestamp,
        MintosCurrency base,
        @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDate date,
        Map<MintosCurrency, BigDecimal> rates
) {
}
