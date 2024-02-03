package com.studykeycloak.service1.global.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
  private final String claimResourceAccess = "resource_access";


  @AllArgsConstructor
  @Getter
  private enum RealmClient {   // 여기에 허용할 client를 등록
    TEST_A("test-a"), TEST_B("test-b");
    private String title;
  }

  
  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    String username = jwt.getClaim("preferred_username");
    String uuid = jwt.getClaim("uuid");
    log.info("username=[{}]", username);

    Collection<GrantedAuthority> authorities = Stream.concat(
      jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
      extractResourceRoles(jwt).stream()).collect(Collectors.toSet()
    );
    
    return new JwtAuthenticationTokenCustom(jwt, authorities, jwt.getClaim(JwtClaimNames.SUB), username, uuid);
  }


  private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim(claimResourceAccess);

    Set<String> resource = new HashSet<>();
    Arrays.stream(RealmClient.values()).forEach(realmClient -> getClientRoles(resource, resourceAccess, realmClient));

    Collection<String> resourceRoles = (Collection<String>) resource;
    if(resourceAccess == null || resourceRoles == null || resource.isEmpty()) {
      return Set.of();
    }
    
    return resourceRoles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet());
  }


  @SuppressWarnings("unchecked")
  private void getClientRoles(Set<String> resource, Map<String, Object> resourceAccess, RealmClient client) {
    if(resourceAccess == null || resourceAccess.get(client.getTitle()) == null) {
      return;
    }

    Map<String, Object> mapRoles = (Map<String, Object>) resourceAccess.get(client.getTitle());
    List<String> roles = (List<String>) mapRoles.get("roles");

    log.info("client [{}] --> roles={}", client.getTitle(), roles);
    resource.addAll(roles);
  }
}
