package com.soskate.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties for CORS settings.
 * Allows flexible configuration via application.properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "soskate.cors")
public class CorsConfigProperties {

    /**
     * List of allowed origins (e.g., http://localhost:4200)
     */
    private List<String> allowedOrigins = List.of("http://localhost:4200", "http://localhost:8081");

    /**
     * List of allowed HTTP methods
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");

    /**
     * List of allowed headers
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * Whether to allow credentials (cookies, authorization headers)
     */
    private boolean allowCredentials = true;

    /**
     * How long (in seconds) the browser should cache the preflight response
     */
    private long maxAge = 3600;
}