package com.studykeycloak.service1.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Builder
@Getter
public class ReqDto {
  @Default
  private String service = "service-1";
  private String message;
}
