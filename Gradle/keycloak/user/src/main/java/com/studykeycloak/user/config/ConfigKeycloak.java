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
  public final static String MY_REALM = "my-realm";
  public final static String CLIENT_TEST_A = "test-a";
  public final static String CLIENT_TEST_B = "test-b";
  
  /*
    Admin Rest-API only
    realm: master
    client: admin-cli
  */
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


  // client's roles - test-a, test-b same.
  @AllArgsConstructor
  @Getter
  public enum ClientRoles {
    USER("user"),
    MANAGER("manager"),
    DIRECTOR("director"),
    ADMIN("admin");

    private final String title;
  }
}
