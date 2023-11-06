package com.jromans.hwk.fx;

import com.jromans.hwk.fx.configuration.Uri;
import com.jromans.hwk.shared.constants.MintosCurrency;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Profile({"prod", "itest", "itest-prod"})
@Service
public class FxRateServiceImpl implements FxService {

    private final Duration TIMEOUT_5_MIN = Duration.of(5, ChronoUnit.SECONDS);

    private final WebClient webClient;

    @Override
    public RateData getRate(MintosCurrency base, MintosCurrency output) {
        var response = getRates(base, List.of(output));
        return getRateData(output, response);
    }

    private ExchangeRateResponse getRates(MintosCurrency baseMintosCurrency, List<MintosCurrency> outputCurrencies) {
        var responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder.pathSegment(Uri.RATES)
                        .queryParam("base", baseMintosCurrency)
                        .queryParam("symbols", outputCurrencies)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().onStatus(HttpStatusCode::isError, clientResponse -> {
                    return Mono.error(new CurrencyConversionServiceError(clientResponse.statusCode()));
                })
                .bodyToMono(ExchangeRateResponse.class)
                .timeout(TIMEOUT_5_MIN)
                .log();


        return responseMono.block();
    }

    @NotNull
    private RateData getRateData(MintosCurrency currency, ExchangeRateResponse response) {
        var rate = response.rates().get(currency);

        var now = LocalTime.now();
        var localDateTime = response.date().atTime(now.getHour(), now.getMinute());

        return new RateData(response.base(), currency, rate, localDateTime);
    }

}
