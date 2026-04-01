package com.soskate.api.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @Value("${stripe.instructor-rate-cents}")
    private int instructorRateCents;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}
