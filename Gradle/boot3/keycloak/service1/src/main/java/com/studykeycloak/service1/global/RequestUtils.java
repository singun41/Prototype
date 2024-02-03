package com.studykeycloak.service1.global;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.studykeycloak.service1.global.security.JwtAuthenticationTokenCustom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestUtils {
  public HttpServletRequest getCurrentRequest() {   // 현재 쓰레드의 Request 객체 가져오기
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
  }


  public String getUserId() {
    Principal principal = getCurrentRequest().getUserPrincipal();
    log.info("principal=[{}]", principal);

    if(principal instanceof JwtAuthenticationTokenCustom customJwt) {
      log.info("user-attribute[uuid]: {}", customJwt.getUuid());
    }

    return ((JwtAuthenticationTokenCustom) principal).getUuid();   // JwtAuthConverter에서 생성할 때 파라미터로 넣은 uuid 값이 나옴.

    // return principal.getName();   // JwtAuthConverter에서 생성할 때 파라미터로 넣은 jwt.getClaim(JwtClaimNames.SUB) 값이 나옴.
  }
}
