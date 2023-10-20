package com.studycloud.service2.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Builder
@Getter
public class ReqDto {
  @Default
  private String service = "service-2";
  private String message;
}
