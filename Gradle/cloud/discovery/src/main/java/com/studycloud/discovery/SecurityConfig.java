package com.studycloud.discovery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
    .httpBasic(httpBasic -> httpBasic.init(http))
    .authorizeHttpRequests(t -> t.anyRequest().authenticated())
    .logout(logout -> logout.logoutUrl("/logout"))
    .csrf(csrf -> csrf.disable())
    .build();
  }
}
