package com.studycloud.gateway.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeGenerator {
  public static LocalDate today() {
    return LocalDate.now(ConfigProperties.ZONE_ID);
  }


  public static LocalDateTime now() {
    return LocalDateTime.now(ConfigProperties.ZONE_ID);
  }


  public static String toString(LocalDate date) {
    return date.format(ConfigProperties.FORMATTER_DATE);
  }


  public static String toString(LocalDateTime datetime) {
    return datetime.format(ConfigProperties.FORMATTER_DATETIME);
  }
}
