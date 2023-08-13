package com.prototype.jwtstudy.common.security;

import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final PBEStringCleanablePasswordEncryptor jasyptStringEncryptor;
  private final JwtProvider jwtProvider;
  private final String urlLogin = "/login";


  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
    .httpBasic(httpBasic -> httpBasic.disable())
    .formLogin(formLogin -> formLogin.disable())
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth ->
      auth.antMatchers(urlLogin).permitAll()
      .anyRequest().authenticated()
    )
    .sessionManagement(sessionMngt -> sessionMngt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
    .build();
  }


  @Bean
  PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();

    return new PasswordEncoder() {   // BCrypt 대신에 커스텀 인코더 사용해보기. Jasypt는 복호화가 가능함. 운영에서는 BCrypt를 사용할 것.
      @Override
      public String encode(CharSequence rawPassword) {
        log.info("rawPassword=[{}]", rawPassword);
        return jasyptStringEncryptor.encrypt(rawPassword.toString());
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.info("rawPassword=[{}] encodedPassword=[{}]", rawPassword, encodedPassword);
        return jasyptStringEncryptor.decrypt(encodedPassword).equals(rawPassword.toString());
      }
    };
  }

  @Bean
  GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults("");   // Remove the ROLE_ prefix
  }
}
