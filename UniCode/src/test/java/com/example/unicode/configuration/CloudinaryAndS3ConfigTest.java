package com.example.unicode.configuration;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class CloudinaryAndS3ConfigTest {

    @Test
    void cloudinaryAndS3BeansShouldBeCreatable() {
        CloudinaryConfig cloudinaryConfig = new CloudinaryConfig();
        ReflectionTestUtils.setField(cloudinaryConfig, "cloudName", "demo");
        ReflectionTestUtils.setField(cloudinaryConfig, "apiKey", "key");
        ReflectionTestUtils.setField(cloudinaryConfig, "apiSecret", "secret");

        Cloudinary cloudinary = cloudinaryConfig.cloudinary();
        S3Config s3Config = new S3Config();

        assertNotNull(cloudinary);
        assertNotNull(s3Config.s3Client());
        assertNotNull(s3Config.s3Presigner());
    }

    @Test
    void s3CredentialsProviderShouldReturnDefaultProviderWhenCredentialsExist() throws Exception {
        S3Config s3Config = new S3Config();
        DefaultCredentialsProvider provider = mock(DefaultCredentialsProvider.class);

        try (MockedStatic<DefaultCredentialsProvider> mocked = mockStatic(DefaultCredentialsProvider.class)) {
            mocked.when(DefaultCredentialsProvider::create).thenReturn(provider);

            Method method = S3Config.class.getDeclaredMethod("credentialsProvider");
            method.setAccessible(true);
            AwsCredentialsProvider result = (AwsCredentialsProvider) method.invoke(s3Config);

            assertSame(provider, result);
            verify(provider).resolveCredentials();
        }
    }
}
