package com.studycloud.service2.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class CustomFilter extends OncePerRequestFilter {
  @Override
  public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    if(req.getRequestURI().contains("/actuator")) {
      chain.doFilter(req, res);
      return;
    }

    String userId = req.getHeader("X-User-Id");
    String strRole = req.getHeader("X-User-Role");
    String[] arrRole = strRole.split(",");
    
    List<GrantedAuthority> authorities = new ArrayList<>();
    for(String role : arrRole)
      authorities.add(new SimpleGrantedAuthority(role));
    
    Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(req, res);
  }
}
