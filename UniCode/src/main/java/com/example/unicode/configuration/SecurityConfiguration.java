package com.example.unicode.configuration;

import lombok.RequiredArgsConstructor;
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

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration  {
  private final CustomDecoder jwtDecoder;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.authorizeHttpRequests(auth -> {
           auth.requestMatchers("/api/auth/login").permitAll()
               .anyRequest().authenticated();
       });

       http.oauth2ResourceServer(
               config -> config.jwt( jwtConfigurer -> {
                   jwtConfigurer
                           .decoder(jwtDecoder)
                   .jwtAuthenticationConverter(jwtAuthenticationConverter());
               })
                       .authenticationEntryPoint(authenticationEntryPoint)
       );
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

}
