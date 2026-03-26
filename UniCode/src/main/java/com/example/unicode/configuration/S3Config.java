package com.example.unicode.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    private AwsCredentialsProvider credentialsProvider() {
        try {
            DefaultCredentialsProvider provider = DefaultCredentialsProvider.create();
            provider.resolveCredentials(); // test if credentials exist
            log.info("AWS credentials loaded successfully.");
            return provider;
        } catch (Exception e) {
            log.warn("⚠ No AWS credentials found ({}). S3 operations will fail at runtime. "
                    + "Set AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.", e.getMessage());
            return AnonymousCredentialsProvider.create();
        }
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(credentialsProvider())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(credentialsProvider())
                .build();
    }
}
