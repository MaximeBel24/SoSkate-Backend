package com.soskate.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Configuration properties for SoSkate security settings.
 *
 * Example usage in application.properties:
 * soskate.security.instructor-activation-token-ttl-hours=48
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "soskate.security")
@Validated
public class SoskateSecurityProperties {

    /**
     * Time-to-live (in hours) for instructor activation tokens.
     * Default: 48 hours
     * Min: 1 hour
     * Max: 168 hours (7 days)
     */
    @Min(value = 1, message = "Activation token TTL must be at least 1 hour")
    @Max(value = 168, message = "Activation token TTL cannot exceed 168 hours (7 days)")
    private int instructorActivationTokenTtlHours = 48;

    /**
     * Length of randomly generated passwords for new instructor accounts.
     * Default: 16 characters
     */
    @Min(value = 12, message = "Generated password length must be at least 12")
    @Max(value = 32, message = "Generated password length cannot exceed 32")
    private int generatedPasswordLength = 16;
}