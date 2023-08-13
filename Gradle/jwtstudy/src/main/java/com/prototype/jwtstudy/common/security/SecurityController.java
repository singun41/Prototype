package com.prototype.jwtstudy.common.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.jwtstudy.common.security.dto.request.ReqDtoLogin;
import com.prototype.jwtstudy.common.security.vo.Jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;


  @PostMapping("/login")
  public ResponseEntity<?> login(@Validated @RequestBody ReqDtoLogin dto, BindingResult result, HttpServletResponse res) throws Exception {
    log.info("{}", dto);

    if(result.hasErrors())
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors().get(0));

    Jwt jwt = securityService.login(dto.getUserId(), dto.getUserPw());
    Cookie bearerToken = new Cookie("Authorization", new StringBuilder(jwt.getGrantType()).append("-").append(jwt.getAccessToken()).toString());
    res.addCookie(bearerToken);

    log.info("{}", jwt);
    return ResponseEntity.ok().body(jwt);
  }
}
