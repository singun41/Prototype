package com.prototype.jwtstudy.common.config;

import java.time.ZoneId;

public class ConfigProperties {
  public static final String UUID_CUSTOM_GENERATOR = "custom-timebased-uuid-generator";
  public static final String UUID_CUSTOM_GENERATOR_PACKAGE = "com.prototype.jwtstudy.common.entity.TimebasedUuidGenerator";
  public static final String COLUMN_DEFINITION_UUID = "binary(16)";

  public static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

  public static final String URL_LOGIN = "/login";
  public static final String STR_AUTHORIZATION = "Authorization";
  public static final String STR_REFRESH_TOKEN = "Refresh-Token";
}
