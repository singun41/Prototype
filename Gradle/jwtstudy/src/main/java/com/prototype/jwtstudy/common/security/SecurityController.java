package com.prototype.jwtstudy.common.security;

// import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.jwtstudy.common.config.ConfigProperties;
import com.prototype.jwtstudy.common.security.dto.request.ReqDtoLogin;
import com.prototype.jwtstudy.common.security.vo.Jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;


  @PostMapping(ConfigProperties.URL_LOGIN)
  public ResponseEntity<?> login(@Validated @RequestBody ReqDtoLogin dto, BindingResult result, HttpServletResponse res) throws Exception {
    log.info("{}", dto);

    if(result.hasErrors())
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors().get(0));

    Jwt jwt = securityService.login(dto.getUserId(), dto.getUserPw());
    res.addHeader(ConfigProperties.STR_AUTHORIZATION, jwt.getAccessToken());
    res.addHeader(ConfigProperties.STR_REFRESH_TOKEN, jwt.getRefreshToken());

    // cookie로 전달할 때
    // Cookie accessToken = new Cookie(ConfigProperties.STR_AUTHORIZATION, jwt.getAccessToken());
    // Cookie refreshToken = new Cookie(ConfigProperties.STR_REFRESH_TOKEN, jwt.getRefreshToken());
    // res.addCookie(accessToken);
    // res.addCookie(refreshToken);

    log.info("User {} logged in succeed.", dto.getUserId());
    return ResponseEntity.ok().body("success.");
  }
}
