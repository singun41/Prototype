package com.studykeycloak.service1.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
  private final String claimResourceAccess = "resource_access";
  private final String roles = "roles";
  private final String clientId = "dev";


  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = Stream.concat(
      jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
      extractResourceRoles(jwt).stream()).collect(Collectors.toSet()
    );
    // return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim(JwtClaimNames.SUB));
    return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim("preferred_username"));   // user id
  }


  @SuppressWarnings("unchecked")
  private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim(claimResourceAccess);
    Map<String, Object> resource;
    Collection<String> resourceRoles;

    if(resourceAccess == null || 
      (resource = (Map<String, Object>) resourceAccess.get(clientId)) == null ||
      (resourceRoles = (Collection<String>) resource.get(roles)) == null
    ) {
      return Set.of();
    }

    log.info("{}", resourceRoles);
    return resourceRoles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet());
  }
}
