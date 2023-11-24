package com.studykeycloak.gateway.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqDtoRefreshToken {
  @Schema(title = "refresh token 문자열")
  private String value;
}
