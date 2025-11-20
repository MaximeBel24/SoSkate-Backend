package com.soskate.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 configuration.
 * Supports two authentication modes:
 * 1. IAM Role (for EC2 deployment) - uses DefaultCredentialsProvider
 * 2. Access Keys (for local development) - uses environment variables
 */
@Configuration
public class AwsS3Config {

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.access-key-id:#{null}}")
    private String accessKeyId;

    @Value("${aws.secret-access-key:#{null}}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    /**
     * Determines which credentials provider to use:
     * - If access keys are provided via env variables, use them (local dev)
     * - Otherwise, use default provider chain (IAM Role for EC2)
     */
    private AwsCredentialsProvider getCredentialsProvider() {
        if (accessKeyId != null && secretAccessKey != null &&
                !accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            // Local development with access keys
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)
            );
        } else {
            // Production with IAM Role (EC2)
            return DefaultCredentialsProvider.create();
        }
    }
}