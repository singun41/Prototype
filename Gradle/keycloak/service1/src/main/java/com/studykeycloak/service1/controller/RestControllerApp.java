package com.studykeycloak.service1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.service1.common.RequestUtils;
import com.studykeycloak.service1.common.ConfigProperties;
import com.studykeycloak.service1.controller.dto.ReqDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestControllerApp {
  private final RequestUtils requestUtils;


  @GetMapping("/all")
  public ResponseEntity<?> accessAll() {
    log.info("endpoint 'all' called.");
    ReqDto dto = ReqDto.builder().message("Hello.").build();
    return ResponseEntity.ok(dto);
  }


  @GetMapping("/user")
  @PreAuthorize(ConfigProperties.USER_ROLE_DEFAULT)
  public ResponseEntity<?> accessUser() {
    log.info("userId=[{}]", requestUtils.getUserId());

    log.info("endpoint 'user' called.");
    ReqDto dto = ReqDto.builder().message("Hello User.").build();
    return ResponseEntity.ok(dto);
  }


  @GetMapping("/manager")
  @PreAuthorize(ConfigProperties.USER_ROLE_MANAGER)
  public ResponseEntity<?> accessManager() {
    log.info("userId=[{}]", requestUtils.getUserId());

    log.info("endpoint 'manager' called.");
    ReqDto dto = ReqDto.builder().message("Hello Manager.").build();
    return ResponseEntity.ok(dto);
  }


  @GetMapping("/admin")
  @PreAuthorize(ConfigProperties.USER_ROLE_ADMIN)
  public ResponseEntity<?> accessAdmin() {
    log.info("userId=[{}]", requestUtils.getUserId());

    log.info("endpoint 'admin' called.");
    ReqDto dto = ReqDto.builder().message("Hello Admin.").build();
    return ResponseEntity.ok(dto);
  }
}
