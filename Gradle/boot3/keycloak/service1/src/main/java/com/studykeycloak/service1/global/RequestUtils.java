package com.studykeycloak.service1.global;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    return principal.getName();   // 유저의 uuid 값이 나온다.
  }
}
