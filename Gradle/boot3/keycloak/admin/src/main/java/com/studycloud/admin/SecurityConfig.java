package com.studycloud.admin;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
    .authorizeHttpRequests(t ->
      t.requestMatchers("/login", "/logout", "/assets/**").permitAll()
      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .anyRequest().authenticated()
    )
    .formLogin(t -> t.loginPage("/login").permitAll().defaultSuccessUrl("/applications"))
    .logout(t -> t.logoutUrl("/logout"))
    .sessionManagement(t -> t.invalidSessionUrl("/login"))
    .csrf(t ->
      t.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .ignoringRequestMatchers("wallboard", "/instances", "/instances/**", "/actuator", "/actuator/**")
    )
    .build();
  }
}
