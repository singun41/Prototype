package com.studykeycloak.gateway.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.gateway.security.dto.request.ReqDtoLogin;
import com.studykeycloak.gateway.security.dto.request.ReqDtoRefreshToken;

import io.swagger.v3.oas.annotations.Operation;
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


  @Operation(summary = "로그인 - client test-a", description = "로그인 API")
  @PostMapping("/login/client/test-a")
  public Mono<ResponseEntity<?>> loginClientTestA(@RequestBody ReqDtoLogin dto) {
    try {
      Mono<ResponseEntity<?>> res = securityService.loginClientTestA(dto);
      return res.onErrorReturn(ResponseEntity.badRequest().body("Invalid credentials.")).flatMap(Mono::just);

    } catch(Exception e) {
      log.error("{}", e.getMessage());
      return Mono.just(ResponseEntity.badRequest().body("Error."));
    }
  }


  @Operation(summary = "토큰 갱신 - client test-a", description = "토큰 갱신하기 : refresh token 입력")
  @PostMapping("/refresh/client/test-a")
  public Mono<ResponseEntity<?>> tokenRefreshClientTestA(@RequestBody ReqDtoRefreshToken dto) {
    try {
      Mono<ResponseEntity<?>> res = securityService.tokenRefreshClientTestA(dto);
      return res.onErrorReturn(ResponseEntity.badRequest().body("Invalid token.")).flatMap(Mono::just);

    } catch(Exception e) {
      log.error("{}", e.getMessage());
      return Mono.just(ResponseEntity.badRequest().body("Error."));
    }
  }





  @Operation(summary = "로그인 - client test-b", description = "로그인 API")
  @PostMapping("/login/client/test-b")
  public Mono<ResponseEntity<?>> loginClientTestB(@RequestBody ReqDtoLogin dto) {
    try {
      Mono<ResponseEntity<?>> res = securityService.loginClientTestB(dto);
      return res.onErrorReturn(ResponseEntity.badRequest().body("Invalid credentials.")).flatMap(Mono::just);

    } catch(Exception e) {
      log.error("{}", e.getMessage());
      return Mono.just(ResponseEntity.badRequest().body("Error."));
    }
  }


  @Operation(summary = "토큰 갱신 - client test-b", description = "토큰 갱신하기")
  @PostMapping("/refresh/client/test-b")
  public Mono<ResponseEntity<?>> tokenRefreshClientTestB(@RequestBody ReqDtoRefreshToken dto) {
    try {
      Mono<ResponseEntity<?>> res = securityService.tokenRefreshClientTestB(dto);
      return res.onErrorReturn(ResponseEntity.badRequest().body("Invalid token.")).flatMap(Mono::just);

    } catch(Exception e) {
      log.error("{}", e.getMessage());
      return Mono.just(ResponseEntity.badRequest().body("Error."));
    }
  }
}
