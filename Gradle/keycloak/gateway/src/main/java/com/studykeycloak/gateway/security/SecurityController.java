package com.studykeycloak.gateway.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.gateway.security.dto.request.ReqDtoLogin;
import com.studykeycloak.gateway.security.dto.request.ReqDtoRefreshToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;


  @PostMapping("/login")
  public Mono<ResponseEntity<?>> login(@RequestBody ReqDtoLogin dto) {
    try {
      Mono<ResponseEntity<?>> res = securityService.login(dto);
      return res.onErrorReturn(ResponseEntity.badRequest().body("Invalid credentials.")).flatMap(Mono::just);

    } catch(Exception e) {
      log.error("{}", e.getMessage());
      return Mono.just(ResponseEntity.badRequest().body("Error."));
    }
  }


  @PostMapping("/refresh")
  public Mono<ResponseEntity<?>> refresh(@RequestBody ReqDtoRefreshToken dto) {
    try {
      Mono<ResponseEntity<?>> res = securityService.refresh(dto);
      return res.onErrorReturn(ResponseEntity.badRequest().body("Invalid token.")).flatMap(Mono::just);

    } catch(Exception e) {
      log.error("{}", e.getMessage());
      return Mono.just(ResponseEntity.badRequest().body("Error."));
    }
  }
}
