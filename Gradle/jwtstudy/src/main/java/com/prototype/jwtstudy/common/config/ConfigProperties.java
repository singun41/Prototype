package com.prototype.jwtstudy.common.config;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConfigProperties {
  public static final String UUID_CUSTOM_GENERATOR = "custom-timebased-uuid-generator";
  public static final String UUID_CUSTOM_GENERATOR_PACKAGE = "com.prototype.jwtstudy.common.entity.TimebasedUuidGenerator";
  public static final String COLUMN_DEFINITION_UUID = "binary(16)";

  public static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
  public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

  public static final String URL_LOGIN = "/login";
  public static final String URL_NEW_ACCESS_TOKEN = "/access-token";
  public static final String URL_NEW_REFRESH_TOKEN = "/refresh-token";
  public static final String URL_TOKEN_EXPIRATION = "/token/expiration";
}
