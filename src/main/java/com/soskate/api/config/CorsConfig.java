package com.soskate.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Global CORS configuration for the SoSkate API.
 * Replaces @CrossOrigin annotations on individual controllers.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CorsConfig {

    private final CorsConfigProperties corsProperties;

    @Bean
    public CorsFilter corsFilter() {
        log.info("Configuring CORS with allowed origins: {}", corsProperties.getAllowedOrigins());

        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins
        corsProperties.getAllowedOrigins().forEach(config::addAllowedOrigin);

        // Allowed methods
        corsProperties.getAllowedMethods().forEach(config::addAllowedMethod);

        // Allowed headers
        corsProperties.getAllowedHeaders().forEach(config::addAllowedHeader);

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(corsProperties.isAllowCredentials());

        // Cache preflight response
        config.setMaxAge(corsProperties.getMaxAge());

        // Apply to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}