package com.studykeycloak.service1.global.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.Getter;

@Getter
public class JwtAuthenticationTokenCustom extends JwtAuthenticationToken {
  private final String username;
  private final String uuid;


  public JwtAuthenticationTokenCustom(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String name, String username, String uuid) {
    super(jwt, authorities, name);
    this.username = username;
    this.uuid = uuid;
  }
}
