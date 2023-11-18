package com.studykeycloak.gateway.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.gateway.security.dto.request.ReqDtoLogin;
import com.studykeycloak.gateway.security.dto.request.ReqDtoRefreshToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Tag(name = "SecurityController", description = "Login & token refresh only")
@Slf4j
@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;


  @Operation(summary = "로그인", description = "로그인 API")
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


  @Operation(summary = "토큰 갱신", description = "토큰 갱신하기")
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
