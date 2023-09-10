package com.prototype.jwtstudy.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prototype.jwtstudy.common.config.ConfigProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {   // GenericFilterBean 대신 이 필터를 상속해야 필터가 2번 동작하는 걸 방지할 수 있다.
  private final JwtProvider jwtProvider;
  private final String headerTryLogin = "Try-Login";
  private final String keyLogin = "kfV2tOtqOxWMHz55";
  private final String keyAuthorization = "Authorization";


  @Override
  public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    if(req.getRequestURI().equals(ConfigProperties.URL_LOGIN)) {
      if(req.getHeader(headerTryLogin).equals(keyLogin))
        log.info("try login.");
      else
        throw new IllegalArgumentException("doFilterInternal() login key is invalid.");

    } else {
      // JWT 추출하기
      String accessToken = getToken(req);
      log.info("doFilterInternal() called. accessToken: {}", accessToken);
      
      if(accessToken == null) {
        // 인증없이 접근할 수 있는 url을 사용할 수 있으므로 주석 처리. SecurityConfig에 인증없이 접근할 수 있는 url을 추가해준다.
        // throw new IllegalArgumentException("doFilterInternal() token is null.");

      } else {
        if(jwtProvider.validation(accessToken)) {
          Authentication authentication = jwtProvider.getAuthentication(accessToken);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        
        } else {   // Access Token이 만료 또는 올바른 토큰이 아닌 경우
          res.addHeader("token-validation", "access-token-expired");
        }
      }
    }

    chain.doFilter(req, res);
  }


  private String getToken(HttpServletRequest req) {
    String token = getTokenFromHeader(req);
    if(token == null)
      return getTokenFromCookie(req);
    return token;
  }


  private String getTokenFromHeader(HttpServletRequest req) {
    log.info("getTokenFromHeader() called.");
    String token = req.getHeader(keyAuthorization);

    if(token == null) {
      return getTokenFromCookie(req);

    } else {
      return token;
    }
  }


  private String getTokenFromCookie(HttpServletRequest req) {
    log.info("getTokenFromCookie() called.");
    Cookie[] cookies = req.getCookies();
    if(cookies == null)
      return null;
    
    for(Cookie cookie : cookies) {
      if(cookie.getName().equals(keyAuthorization))
        return cookie.getValue();
    }
    return null;
  }
}
