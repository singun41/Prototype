package com.prototype.sessionredis.system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.prototype.sessionredis.vo.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler authSuccessHandler;   // 구현체 CustomAuthSuccessHandler 클래스가 주입된다.
    private final AuthenticationFailureHandler authFailureHandler;   // 구현체 CustomAuthFailureHandler 클래스가 주입된다.
    private final SessionRegistry sessionRegistry;   // RedisConfig 클래스에서 SpringSessionBackedSessionRegistry 를 구현한 bean 클래스가 주입된다.

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, AuthenticationSuccessHandler authSuccessHandler, AuthenticationFailureHandler authFailureHandler, SessionRegistry sessionRegistry) {
        this.userDetailsService = userDetailsService;
        this.authSuccessHandler = authSuccessHandler;
        this.authFailureHandler = authFailureHandler;
        this.sessionRegistry = sessionRegistry;

        log.info("Configuration " + this.getClass().getName() + " has been created.");
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info(this.getClass().getName() + "'s mothod 'filterChain' called.");

        http
            .authorizeRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()   // 정적 리소스들에 대한 접근 허가를 처리함
            .antMatchers(Constants.URL_LOGIN).permitAll()
            .anyRequest().authenticated()
        
        .and()
            .csrf().disable()
            .cors().disable()
            .httpBasic()
        
        .and()
            .formLogin().loginPage(Constants.URL_LOGIN).loginProcessingUrl(Constants.URL_LOGIN)
            .usernameParameter("id").passwordParameter("pw")
            .successHandler(authSuccessHandler).failureHandler(authFailureHandler)
        
        .and()
            .userDetailsService(userDetailsService)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .invalidSessionUrl(Constants.URL_LOGIN)
            .maximumSessions(1)   // 같은 유저의 세션은 1개만 허용된다.
            .maxSessionsPreventsLogin(false)   // 새로 접속할 경우 기존 세션은 끊어진다. true로 전환하면 기존 세션이 존재하는 상태의 경우 새로운 접속이 모두 막힘.
            .sessionRegistry(sessionRegistry)
            .expiredUrl(Constants.URL_LOGIN)
        
        .and().and()
            .logout()
            .invalidateHttpSession(true).deleteCookies("SESSION").clearAuthentication(true);

        return http.build();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        log.info("Bean 'BCryptPasswordEncoder' has been created.");
        return new BCryptPasswordEncoder();
    }
}
