package com.prototype.jwtstudy.presentation.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControllerMain {
  @GetMapping("/main")
  public ResponseEntity<?> main() {
    return ResponseEntity.ok("JWT study project.");
  }
  

  @PreAuthorize("hasAnyRole('ADMIN')")
  @GetMapping("/admin")
  public ResponseEntity<?> adminOnly() {
    return ResponseEntity.ok("admin only");
  }


  @GetMapping("/manager")
  @PreAuthorize("hasAnyRole('MANAGER')")
  public ResponseEntity<?> managerOnly() {
    return ResponseEntity.ok("manager only");
  }
}
