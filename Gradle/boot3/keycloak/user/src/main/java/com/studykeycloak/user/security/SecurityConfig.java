package com.studykeycloak.user.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityConfig {
  @Bean
  @Profile("!local")
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("SecurityWebFilterChain bean created. type --> default");

    return http
    .httpBasic(t -> t.disable())
    .formLogin(t -> t.disable())
    .csrf(t -> t.disable())
    .authorizeHttpRequests(t -> t.anyRequest().permitAll())
    .build();
  }


  @Bean
  @Profile("local")
  SecurityFilterChain filterChainLocalOnly(HttpSecurity http) throws Exception {
    log.info("SecurityWebFilterChain bean created. type --> local dev only");
    
    return http
    .httpBasic(t -> t.disable())
    .formLogin(t -> t.disable())
    .csrf(csrf -> csrf.disable())
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .authorizeHttpRequests(t -> t.anyRequest().permitAll())
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
