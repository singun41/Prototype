package com.studykeycloak.service1.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.service1.common.ConfigProperties;
import com.studykeycloak.service1.controller.dto.ReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RestControllerApp {
  @GetMapping("/all")
  public ResponseEntity<?> accessAll() {
    log.info("endpoint 'all' called.");
    ReqDto dto = ReqDto.builder().message("Hello.").build();
    return ResponseEntity.ok(dto);
  }


  @GetMapping("/manager")
  @PreAuthorize(ConfigProperties.USER_ROLE_MANAGER)
  public ResponseEntity<?> accessManager(HttpServletRequest req) {
    log.info("userId=[{}]", req.getUserPrincipal().getName());

    log.info("endpoint 'manager' called.");
    ReqDto dto = ReqDto.builder().message("Hello Manager.").build();
    return ResponseEntity.ok(dto);
  }


  @GetMapping("/admin")
  @PreAuthorize(ConfigProperties.USER_ROLE_ADMIN)
  public ResponseEntity<?> accessAdmin(HttpServletRequest req) {
    log.info("userId=[{}]", req.getUserPrincipal().getName());

    log.info("endpoint 'admin' called.");
    ReqDto dto = ReqDto.builder().message("Hello Admin.").build();
    return ResponseEntity.ok(dto);
  }
}
