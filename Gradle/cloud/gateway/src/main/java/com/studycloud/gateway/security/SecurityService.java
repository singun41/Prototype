package com.studycloud.gateway.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.studycloud.gateway.security.dto.response.ResDtoToken;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityService {
  private final ReactiveAuthenticationManager reactiveAuthManager;
  private final JwtProvider jwtProvider;


  Mono<ResponseEntity<?>> login(String userId, String userPw) throws Exception {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, userPw);
    return reactiveAuthManager.authenticate(authToken).map(
      jwtProvider::generate
    ).map(
      jwt -> ResponseEntity.ok(
        ResDtoToken.builder()
        .access(jwt.getAccessToken())
        .refresh(jwt.getRefreshToken())
        .message("success")
        .build()
      )
    );
  }


  Mono<ResponseEntity<?>> getNewAccessToken(String refreshToken) {
    return Mono.just(ResponseEntity.ok(jwtProvider.getNewAccessToken(refreshToken)));
  }


  Mono<ResponseEntity<?>> getNewRefreshToken(String refreshToken) {
    return Mono.just(ResponseEntity.ok(jwtProvider.getNewRefreshToken(refreshToken)));
  }


  Mono<ResponseEntity<?>> getTokenExpirationDatetime(String refreshToken) {
    return Mono.just(ResponseEntity.ok(jwtProvider.getTokenExpirationDatetime(refreshToken)));
  }
}
