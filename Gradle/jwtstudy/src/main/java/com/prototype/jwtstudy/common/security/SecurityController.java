package com.prototype.jwtstudy.common.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.jwtstudy.common.config.ConfigProperties;
import com.prototype.jwtstudy.common.security.dto.request.ReqDtoLogin;
import com.prototype.jwtstudy.common.security.dto.response.ResDtoToken;
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
    if(result.hasErrors())
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors().get(0));

    Jwt jwt = securityService.login(dto.getUserId(), dto.getUserPw());
    ResDtoToken resDtoToken = ResDtoToken.builder().access(jwt.getAccessToken()).refresh(jwt.getRefreshToken()).message("success").build();

    log.info("User logged in. --> {}", dto.getUserId());
    return ResponseEntity.ok(resDtoToken);
  }


  @GetMapping(ConfigProperties.URL_NEW_ACCESS_TOKEN)
  public ResponseEntity<?> getNewAccessToken(@RequestParam String token) {
    String newAccessToken = securityService.getNewAccessToken(token);
    if(newAccessToken == null) {
      return ResponseEntity.badRequest().body(ResDtoToken.builder().message("Error").build());
    
    } else {
      return ResponseEntity.ok(ResDtoToken.builder().access(newAccessToken).message("Success").build());
    }
  }


  @GetMapping(ConfigProperties.URL_NEW_REFRESH_TOKEN)
  public ResponseEntity<?> getNewRefreshToken(@RequestParam String token) {
    String newRefreshToken = securityService.getNewRefreshToken(token);
    if(newRefreshToken == null) {
      return ResponseEntity.badRequest().body(ResDtoToken.builder().message("Error").build());

    } else {
      return ResponseEntity.ok(ResDtoToken.builder().refresh(newRefreshToken).message("Success").build());
    }
  }


  @GetMapping(ConfigProperties.URL_TOKEN_EXPIRATION)
  public ResponseEntity<?> getTokenExpirationDatetime(@RequestParam String token) {
    String expDt = securityService.getTokenExpirationDatetime(token);
    if(expDt == null) {
      return ResponseEntity.badRequest().body("Error");
    
    } else {
      return ResponseEntity.ok(expDt);
    }
  }
}
