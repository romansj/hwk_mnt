package com.jromans.hwk.apilayer;

import com.jromans.hwk.fx.CurrencyConversionService;
import com.jromans.hwk.fx.FxServiceFake;
import com.jromans.hwk.fx.db.ExchangeRate;
import com.jromans.hwk.fx.db.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jromans.hwk.shared.constants.MintosCurrency.*;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class FxServiceTest {

    @Mock
    RateRepository rateRepository;

    @Spy
    private final FxServiceFake fxServiceFake = new FxServiceFake();

    @InjectMocks
    CurrencyConversionService service;

    @BeforeEach
    void setUp() {
        var exchangeRate = new ExchangeRate(EUR, USD, valueOf(0.5), LocalDateTime.of(1970, 1, 1, 0, 0));

        MockitoAnnotations.openMocks(this);

        doReturn(Optional.of(exchangeRate)).when(rateRepository).save(any());
        doReturn(Optional.of(exchangeRate)).when(rateRepository).findByBaseAndOutputOrderByDateTimeDesc(any(), any());
    }

    @Test
    void cannotRequestToConvertTheSameCurrency() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.getRate(EUR, EUR);
        });
    }

    @Test
    void cannotRequestToConvertFromUnknown() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.getRate(null, EUR);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            service.getRate(EUR, null);
        });
    }

    @Test
    void canRequestToConvertDifferentCurrencies() {
        assertDoesNotThrow(() -> {
            service.getRate(EUR, USD);
            service.getRate(USD, EUR);
            service.getRate(DKK, EUR);
        });
    }
}