package com.prototype.jwtstudy.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prototype.jwtstudy.common.security.vo.Jwt;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
  private final AuthenticationManagerBuilder authManagerBuilder;
  private final JwtProvider jwtProvider;


  Jwt login(String userId, String userPw) throws Exception {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, userPw);
    Authentication auth = authManagerBuilder.getObject().authenticate(authToken);
    Jwt jwt = jwtProvider.generate(auth);
    return jwt;
  }
}
