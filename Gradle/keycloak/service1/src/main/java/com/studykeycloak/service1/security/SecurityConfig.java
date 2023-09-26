package com.studykeycloak.service1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthConverter jwtAuthConverter;


  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
    .httpBasic(httpBasic -> httpBasic.disable())
    .formLogin(formLogin -> formLogin.disable())
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
    .oauth2ResourceServer(customizer -> customizer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
    .sessionManagement(sessionMngt -> sessionMngt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .build();
  }

  @Bean
  GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults("");   // Remove the ROLE_ prefix
  }
}
