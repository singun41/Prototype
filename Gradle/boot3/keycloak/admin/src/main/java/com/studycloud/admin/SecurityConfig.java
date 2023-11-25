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

    /*
      spring boot admin 자체에서 js나 css를 제공할 때 http로 전달하기 때문에 https 환경인 경우
      브라우저의 콘솔에서 mixed content 경고가 나오면서 접속이 되지 않는다.
      해결하기 위해선 아래 헤더를 추가해줘야 한다.
      참고: https://stackoverflow.com/questions/72035591/spring-boot-admin-mixed-content-and-not-show-styles-in-server-https
    */
    .headers(t -> t.contentSecurityPolicy(x -> x.policyDirectives("upgrade-insecure-requests")))

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
