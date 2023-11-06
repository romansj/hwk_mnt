package com.jromans.hwk.fx.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "hwk.api-layer")
public class ApiLayerAccess {
    private String apiKey;
    private String baseUrl;
}
