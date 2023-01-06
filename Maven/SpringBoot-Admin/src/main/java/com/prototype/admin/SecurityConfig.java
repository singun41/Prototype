package com.prototype.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    private final AuthenticationSuccessHandler successHandler;

    @Autowired
    public SecurityConfig(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
            .and()
            .authorizeRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .antMatchers("/login", "/logout").permitAll()
            .antMatchers("/assets/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringAntMatchers("/instances", "/instances/**", "/actuator", "/actuator/**")
            .and()
            .formLogin().loginPage("/login").loginProcessingUrl("/login").successHandler(successHandler)
            .and()
            .logout().invalidateHttpSession(true)
            .and()
            .sessionManagement().invalidSessionUrl("/login");
        return http.build();
    }
}
