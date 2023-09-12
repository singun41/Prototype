package com.studycloud.gateway.security.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResDtoToken {
  private String access;
  private String refresh;
  private String message;
}
