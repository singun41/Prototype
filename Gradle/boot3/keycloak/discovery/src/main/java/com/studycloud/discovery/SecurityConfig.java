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
    .httpBasic(t -> t.init(http))
    .authorizeHttpRequests(t -> t.anyRequest().authenticated())
    // .formLogin(t -> t.loginPage("/login").permitAll().defaultSuccessUrl("/"))
    // form 로그인하면 Discovery Eureka Client 마이크로 서비스들이 접속하지 못한다.
    .logout(t -> t.logoutUrl("/logout"))
    .csrf(t -> t.disable())
    .build();
  }
}
