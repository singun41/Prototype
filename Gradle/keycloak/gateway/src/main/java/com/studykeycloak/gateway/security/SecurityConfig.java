package com.studykeycloak.gateway.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class SecurityConfig {
  @Bean
  @Profile("!local")
  SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
    log.info("SecurityWebFilterChain bean created. type --> default");

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

  
  @Bean
  @Profile("local")
  SecurityWebFilterChain filterChainLocalOnly(ServerHttpSecurity http) {
    log.info("SecurityWebFilterChain bean created. type --> local dev only");

    return http
    .exceptionHandling(exceptionHandlingSpec ->
      exceptionHandlingSpec
      .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
      .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
    )
    .httpBasic(httpBasic -> httpBasic.disable())
    .formLogin(formLogin -> formLogin.disable())
    .csrf(csrf -> csrf.disable())
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
    .build();
  }


  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*")); // 모든 origin 허용
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.addAllowedHeader("*"); // 모든 header 허용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
