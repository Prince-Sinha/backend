package com.issuetracker.opinionservice.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        // Retry up to 3 times with increasing intervals
        // Starting at 2 seconds, max 5 seconds, max 3 attempts
        return new Retryer.Default(
            2000,  // Initial interval (2 seconds)
            5000,  // Max interval (5 seconds)
            3      // Max attempts (total = 3)
        );
    }

    @Bean
    public Request.Options options() {
        // Increase connection and read timeouts (in milliseconds)
        return new Request.Options(
            10000,  // Connect timeout (10 seconds)
            60000   // Read timeout (60 seconds)
        );
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // Log all request/response details
    }
}
