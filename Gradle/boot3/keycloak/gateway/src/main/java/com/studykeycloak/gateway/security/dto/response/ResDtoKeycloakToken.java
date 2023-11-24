package com.studykeycloak.gateway.security.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ResDtoKeycloakToken {
  private String access_token;
  private String refresh_token;
  private String token_type;
}
