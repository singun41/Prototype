package com.prototype.jwtstudy.common.security.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class Jwt {
  private String grantType;
  private String accessToken;
  private String refreshToken;
}
