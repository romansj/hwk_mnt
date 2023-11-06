package com.jromans.hwk.fx;

import com.jromans.hwk.shared.constants.MintosCurrency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.valueOf;

@Slf4j
@ConditionalOnMissingBean(FxRateServiceImpl.class)
@Component
public class FxServiceFake implements FxService {

    private final LocalDateTime year1970 = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

    @Override
    public RateData getRate(MintosCurrency base, MintosCurrency output) {
        var rates = getRates(base);
        log.info("Returning faked rates [base: {}], [output:{}] -- {}", base, output, rates);
        var rate = rates.get(output);

        return new RateData(base, output, rate, year1970);
    }

    private Map<MintosCurrency, BigDecimal> getRates(MintosCurrency baseMintosCurrency) {

        if (baseMintosCurrency == MintosCurrency.EUR) {
            var rates = new HashMap<MintosCurrency, BigDecimal>();
            rates.put(MintosCurrency.USD, valueOf(1.0567640827));
            rates.put(MintosCurrency.GBP, valueOf(0.87));
            rates.put(MintosCurrency.DKK, valueOf(7.46));
            return rates;

        } else if (baseMintosCurrency == MintosCurrency.USD) {
            var rates = new HashMap<MintosCurrency, BigDecimal>();
            rates.put(MintosCurrency.EUR, valueOf(0.946285));
            rates.put(MintosCurrency.GBP, valueOf(0.823145));
            rates.put(MintosCurrency.DKK, valueOf(7.09));
            return rates;
        }


        var rates = new HashMap<MintosCurrency, BigDecimal>();
        rates.put(MintosCurrency.USD, BigDecimal.valueOf(0.5));
        rates.put(MintosCurrency.GBP, BigDecimal.valueOf(0.5));
        rates.put(MintosCurrency.DKK, BigDecimal.valueOf(0.5));
        rates.put(MintosCurrency.CAD, BigDecimal.valueOf(0.5));
        rates.put(MintosCurrency.AUD, BigDecimal.valueOf(0.5));
        rates.put(MintosCurrency.EUR, BigDecimal.valueOf(0.5));
        return rates;

    }


}
