package com.studykeycloak.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@EnableWebSecurity
@OpenAPIDefinition(servers = @Server(url = "http://localhost:8080/service-user", description = "Service-User"))   // prod 환경에서는 해당 URL을 입력, gateway에서 predicates로 처리하는 접미사 url /service-user 추가해줘야 함.
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
