package com.prototype.jwtstudy.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
  private final JwtProvider jwtProvider;
  private final String prefixBearer = "Bearer-";
  private final String strAuthorization = "Authorization";


  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    // JWT 추출하기
    String token = getToken(req);
    if(token != null && jwtProvider.validation(token)) {
      Authentication authentication = jwtProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(req, res);
  }


  private String getToken(ServletRequest req) {
    String token = getTokenFromCookie(req);
    if(token == null)
      return getTokenFromHeader(req);
    return token;
  }


  private String getTokenFromCookie(ServletRequest req) {
    Cookie[] cookies = ((HttpServletRequest) req).getCookies();
    if(cookies == null)
      return null;
    
    for(Cookie cookie : cookies) {
      if(cookie.getName().equals(strAuthorization)) {
        return cookie.getValue().replace(prefixBearer, "");   // "Bearer-"를 제거하고 accessToken만 가져온다.
      }
    }

    return null;
  }


  private String getTokenFromHeader(ServletRequest req) {
    String token = ((HttpServletRequest) req).getHeader(strAuthorization);
    if(token.startsWith(prefixBearer))
      return token.replace(prefixBearer, "");   // "Bearer-"를 제거하고 accessToken만 가져온다.
    else
      return null;
  }
}
