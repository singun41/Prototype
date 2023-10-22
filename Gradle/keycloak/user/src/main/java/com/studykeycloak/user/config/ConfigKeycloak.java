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
  public final static String MY_CLIENT_ID = "bdbd78da-ac28-4f19-bc78-54ea35a81781";

  // dev client's roles
  public final static String PREPARED_ROLE_USER_ID = "2427bb8d-e4bd-40b3-b7e4-6eacdbadbfe6";
  public final static String PREPARED_ROLE_USER_NAME = "user";
  public final static String PREPARED_ROLE_MANAGER_ID = "e94a7f56-56ad-4c44-84ca-97444b693a68";
  public final static String PREPARED_ROLE_MANAGER_NAME = "manager";
  public final static String PREPARED_ROLE_ADMIN_ID = "87c3697d-c867-4b78-9aa5-f942332bec09";
  public final static String PREPARED_ROLE_ADMIN_NAME = "admin";
  
  // admin
  private final String master = "master";
  private final String adminCli = "admin-cli";
  private final String secret = "aO5TySo3eDwEIsGjCzUBcCjaVEL2J74O";
  private final String username = "admin";
  private final String password = "Eq123!@#";


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
    USER(PREPARED_ROLE_USER_ID, PREPARED_ROLE_USER_NAME),
    MANAGER(PREPARED_ROLE_MANAGER_ID, PREPARED_ROLE_MANAGER_NAME),
    ADMIN(PREPARED_ROLE_ADMIN_ID, PREPARED_ROLE_ADMIN_NAME);

    private final String id;
    private final String title;
  }
}
