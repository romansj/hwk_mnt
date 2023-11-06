package com.jromans.hwk.fx;

import com.jromans.hwk.fx.db.ExchangeRate;
import com.jromans.hwk.fx.db.RateRepository;
import com.jromans.hwk.shared.constants.MintosCurrency;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.math.BigDecimal.ONE;

@AllArgsConstructor
@Slf4j
@Transactional
@Service
public class CurrencyConversionService {

    private final FxService fxService;
    private final RateRepository rateRepository;

    /**
     * Any input and response validation, saving goes here, while fetching data (real or fake) is done separately
     */
    public BigDecimal getRate(MintosCurrency base, MintosCurrency output) {
        if (base == null || output == null) throw new IllegalArgumentException("Requested currency is null");
        if (base == output) throw new IllegalArgumentException("Requested output currency is the same as base");

        RateData rateData;
        try {
            rateData = fxService.getRate(base, output);
            saveRate(rateData);

        } catch (Exception e) {
            log.error("Exception while getting fx rate: {}", e.getMessage());
            rateData = getLastSavedRate(base, output);
        }

        return rateData.rate();
    }

    private RateData getLastSavedRate(MintosCurrency base, MintosCurrency output) {
        return rateRepository.findByBaseAndOutputOrderByDateTimeDesc(base, output)
                .map(this::getRateData)
                .orElseThrow(() -> new IllegalStateException("Could not get last saved rate"));
    }


    /**
     * Saves given data and opposite (EUR->USD and USD->EUR) by inverting the rate
     *
     * @param rateData RateData to store in database
     */
    private void saveRate(RateData rateData) {
        log.info("Saving RateData {}", rateData);

        var dateTime = rateData.dateTime();
        var output = rateData.output();
        var base = rateData.base();

        rateRepository.saveAll(List.of(
                new ExchangeRate(base, output, rateData.rate(), dateTime),
                new ExchangeRate(output, base, ONE.divide(rateData.rate(), RoundingMode.HALF_UP), dateTime))
        );
    }

    @NotNull
    private RateData getRateData(ExchangeRate data) {
        return new RateData(data.getBase(), data.getOutput(), data.getRate(), data.getDateTime());
    }
}
