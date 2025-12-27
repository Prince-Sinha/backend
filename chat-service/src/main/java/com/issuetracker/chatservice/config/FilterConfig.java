package com.issuetracker.chatservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for servlet filters
 */
@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    
    private final AuthenticationFilter authenticationFilter;
    
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authFilter() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/api/v1/chat/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}