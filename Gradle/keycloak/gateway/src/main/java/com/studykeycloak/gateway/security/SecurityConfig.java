package com.studykeycloak.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
    .exceptionHandling(exceptionHandlingSpec ->
      exceptionHandlingSpec
      .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
      .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
    )
    .httpBasic(httpBasic -> httpBasic.disable())
    .formLogin(formLogin -> formLogin.disable())
    .csrf(csrf -> csrf.disable())
    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
    .build();
  }
}
