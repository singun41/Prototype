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
    .authorizeHttpRequests(t -> t
      .antMatchers("/login", "/logout", "/assets/**").permitAll()
      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .anyRequest().authenticated()
    )
    .formLogin(formLogin -> formLogin.loginPage("/login").permitAll().defaultSuccessUrl("/applications"))
    .sessionManagement(t -> t.invalidSessionUrl("/login"))
    .csrf(csrf -> csrf
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .ignoringAntMatchers("wallboard", "/instances", "/instances/**", "/actuator", "/actuator/**")
    )
    .build();
  }
}
