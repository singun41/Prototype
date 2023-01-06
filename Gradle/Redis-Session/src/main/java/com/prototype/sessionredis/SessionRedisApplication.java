package com.prototype.sessionredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableWebSecurity
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 5)
public class SessionRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionRedisApplication.class, args);
	}

}
