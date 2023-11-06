package com.jromans.hwk.apilayer;

import com.jromans.hwk.fx.FxRateServiceImpl;
import com.jromans.hwk.fx.configuration.ApiLayerAccess;
import com.jromans.hwk.fx.configuration.ApiLayerConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.text.MessageFormat;

import static com.jromans.hwk.shared.constants.MintosCurrency.EUR;
import static com.jromans.hwk.shared.constants.MintosCurrency.USD;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@ActiveProfiles("itest")
@SpringBootTest(classes = {FxRateServiceImpl.class, ApiLayerConfiguration.class, ApiLayerAccess.class})
class FxRateServiceImplTest {

    @Autowired
    private FxRateServiceImpl fxRateService;

    @Test
    void getRate() {
        BigDecimal rate = fxRateService.getRate(EUR, USD).rate();
        System.out.println(MessageFormat.format("rate {0}", rate));
        assertNotNull(rate);
    }
}