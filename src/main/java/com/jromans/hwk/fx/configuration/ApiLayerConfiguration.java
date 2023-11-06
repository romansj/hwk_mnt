package com.jromans.hwk.fx.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
@Configuration
public class ApiLayerConfiguration {

    private final ApiLayerAccess apiLayerAccess;

    @Bean
    public WebClient webClient() {
        var apiKey = apiLayerAccess.getApiKey();
        var baseUrl = apiLayerAccess.getBaseUrl();


        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("apikey", apiKey)
                .build();
    }

}
