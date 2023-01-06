package com.prototype.sessionredis.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfig {
    public RedisConfig() {
        log.info("Configuration " + this.getClass().getName() + " has been created.");
    }

    @Value("${spring.redis.host}")
    public String host;

    @Value("${spring.redis.port}")
    public int port;

    @Value("${spring.redis.password}")
    public String pw;

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        log.info("Bean RedisTemplate has been created.");

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        log.info("Bean RedisConnectionFactory has been created.");

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(pw);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    SpringSessionBackedSessionRegistry<? extends Session> springSessionBackedSessionRegistry(RedisIndexedSessionRepository redisIndexedSessionRepository) {
        log.info("Bean SpringSessionBackedSessionRegistry has been created.");
        return new SpringSessionBackedSessionRegistry<>(redisIndexedSessionRepository);
    }
}
