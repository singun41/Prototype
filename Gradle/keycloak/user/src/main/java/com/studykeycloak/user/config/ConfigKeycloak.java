package com.studykeycloak.user.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Configuration
public class ConfigKeycloak {
  public final static String URL_SERVER = "http://keycloak-2203.local-cubi5.com";

  // custom realm and client
  public final static String MY_REALM = "keycloak-2203";
  public final static String MY_CLIENT_NAME = "dev";
  public final static String MY_CLIENT_ID = "9636c720-f959-446a-a7ac-57d413f3bb38";

  // dev client's roles
  // public final static String PREPARED_ROLE_USER_ID = "93a40b90-bb93-417c-94bc-878b97742cd5";
  public final static String PREPARED_ROLE_USER_NAME = "user";
  
  // public final static String PREPARED_ROLE_MANAGER_ID = "2c0793a1-9798-4826-9cd7-50ff27ff060a";
  public final static String PREPARED_ROLE_MANAGER_NAME = "manager";

  // public final static String PREPARED_ROLE_DIRECTOR_ID = "c9c2aebb-a1c9-4448-8554-15301ea38331";
  public final static String PREPARED_ROLE_DIRECTOR_NAME = "director";

  // public final static String PREPARED_ROLE_ADMIN_ID = "4d5f4fc2-e7f3-4da4-9e94-29c38227edbd";
  public final static String PREPARED_ROLE_ADMIN_NAME = "admin";
  
  // admin
  private final String master = "master";
  private final String adminCli = "admin-cli";
  private final String secret = "MUYugjdYZ9t5Q4z4w8mwXGJZSZ7tLIxd";
  private final String username = "admin";
  private final String password = "admin12!@";


  @Bean
  Keycloak keycloak() {
    return KeycloakBuilder.builder()
    .serverUrl(URL_SERVER)
    .realm(master)
    .clientId(adminCli)
    .clientSecret(secret)
    .username(username)
    .password(password)
    .build();
  }


  @AllArgsConstructor
  @Getter
  public enum PreparedRole {
    USER(/* PREPARED_ROLE_USER_ID, */ PREPARED_ROLE_USER_NAME),
    MANAGER(/* PREPARED_ROLE_MANAGER_ID, */ PREPARED_ROLE_MANAGER_NAME),
    DIRECTOR(/* PREPARED_ROLE_DIRECTOR_ID, */ PREPARED_ROLE_DIRECTOR_NAME),
    ADMIN(/* PREPARED_ROLE_ADMIN_ID, */ PREPARED_ROLE_ADMIN_NAME);

    // private final String id;
    private final String title;
  }
}
