package com.example.unicode.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final CustomDecoder jwtDecoder;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Value("${login.google.success-url}")
    private String SUCCESS_URL;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(
                            "/api/auth/login",
                            "/api/auth/login-google",
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api/files/upload","/api/v1/subscriptions/momo/call-back"
                    ).permitAll()
                    // Public read-only endpoints for courses and syllabuses
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/courses").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/courses/**").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/syllabuses").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/syllabuses/**").permitAll()
                    .anyRequest().authenticated();
        });

        http.oauth2ResourceServer(
                config -> config.jwt(jwtConfigurer -> {
                            jwtConfigurer
                                    .decoder(jwtDecoder)
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter());
                        })
                        .authenticationEntryPoint(authenticationEntryPoint)
        );
        http.oauth2Login(config -> {
            config.defaultSuccessUrl(SUCCESS_URL, true);
        });
        http.cors((cor)->cor.configurationSource(corsConfiguration()));
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
        granted.setAuthorityPrefix("");
        JwtAuthenticationConverter converter2 = new JwtAuthenticationConverter();
        converter2.setJwtGrantedAuthoritiesConverter(granted);
        return converter2;
    }
  @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setAllowCredentials(true);
        CorsConfigurationSource c = request -> corsConfig;
        return c;
    }
}
