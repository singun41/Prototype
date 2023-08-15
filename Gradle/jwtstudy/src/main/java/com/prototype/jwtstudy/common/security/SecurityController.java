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
  private final String strAuthorization = "Authorization";
  private final String strRefreshToken = "refreshToken";


  @PostMapping("/login")
  public ResponseEntity<?> login(@Validated @RequestBody ReqDtoLogin dto, BindingResult result, HttpServletResponse res) throws Exception {
    log.info("{}", dto);

    if(result.hasErrors())
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors().get(0));

    Jwt jwt = securityService.login(dto.getUserId(), dto.getUserPw());
    String strAccessToken = new StringBuilder(jwt.getGrantType()).append("-").append(jwt.getAccessToken()).toString();

    // Cookie accessToken = new Cookie(strAuthorization, strAccessToken);
    // Cookie refreshToken = new Cookie(strRefreshToken, jwt.getRefreshToken());
    // res.addCookie(accessToken);
    // res.addCookie(refreshToken);

    res.addHeader(strAuthorization, strAccessToken);
    res.addHeader(strRefreshToken, jwt.getRefreshToken());

    log.info("{}", jwt);
    return ResponseEntity.ok().body(jwt);
  }
}
