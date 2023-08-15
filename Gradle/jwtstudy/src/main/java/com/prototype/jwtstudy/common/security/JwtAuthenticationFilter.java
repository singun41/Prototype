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

import com.prototype.jwtstudy.common.security.vo.Jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {   // GenericFilterBean 대신 이 필터를 상속해야 필터가 2번 동작하는 걸 방지할 수 있다.
  private final JwtProvider jwtProvider;
  private final String prefixBearer = "Bearer-";
  private final String strAuthorization = "Authorization";
  private final String strRefreshToken = "refreshToken";


  @Override
  public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    // JWT 추출하기
    String accessToken = getToken(req);
    log.info("doFilterInternal() called. accessToken: {}", accessToken);
    
    if(accessToken == null)
      throw new IllegalArgumentException("doFilterInternal() token is null.");

    if(jwtProvider.validation(accessToken)) {
      Authentication authentication = jwtProvider.getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    
    } else {
      String refreshToken = getRefreshToken(req);
      if(refreshToken == null) {
        log.warn("refresh token is null");
      
      } else {
        if(jwtProvider.validation(refreshToken)) {
          log.info("valid refresh token. access token regenerate.");
          Jwt jwt = jwtProvider.tokenRegenerate(refreshToken);
          if(jwt != null) {
            String strAccessToken = new StringBuilder(jwt.getGrantType()).append("-").append(jwt.getAccessToken()).toString();
            // Cookie accessToken = new Cookie("Authorization", strAccessToken);
            // res.addCookie(accessToken);
            res.addHeader(strAuthorization, strAccessToken);
          }

        } else {
          log.warn("refresh token is invalid.");
          jwtProvider.removeCache(refreshToken);
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
    String token = req.getHeader(strAuthorization);

    if(token == null) {
      return getTokenFromCookie(req);

    } else {
      if(token.startsWith(prefixBearer))
        return token.replace(prefixBearer, "");   // "Bearer-"를 제거하고 accessToken만 가져온다.
      else
        return null;
    }
  }


  private String getTokenFromCookie(HttpServletRequest req) {
    log.info("getTokenFromCookie() called.");
    Cookie[] cookies = req.getCookies();
    if(cookies == null)
      return null;
    
    for(Cookie cookie : cookies) {
      if(cookie.getName().equals(strAuthorization)) {
        return cookie.getValue().replace(prefixBearer, "");   // "Bearer-"를 제거하고 accessToken만 가져온다.
      }
    }

    return null;
  }


  private String getRefreshToken(HttpServletRequest req) {
    log.info("getRefreshToken() called.");
    return req.getHeader(strRefreshToken);
  }
}
