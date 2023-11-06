package com.jromans.hwk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class HwkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HwkApplication.class, args);
    }

}
