package com.vikram.blogapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikram.blogapp.filter.JWTAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // Disable it from being called twice by preventing it from adding it to servlet container filters
    @Bean
    public FilterRegistrationBean<JWTAuthFilter> registrationJWTAuthFilter(JWTAuthFilter filter) {
        FilterRegistrationBean<JWTAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
