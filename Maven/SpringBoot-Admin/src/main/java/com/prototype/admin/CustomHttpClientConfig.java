package com.prototype.admin;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Configuration
public class CustomHttpClientConfig {   // client webapp이 https 인 경우 SSL Handshake를 무시하기 위해 추가.
    @Bean
    ClientHttpConnector customHttpClient() throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create().secure( ssl -> ssl.sslContext(sslContext) );
        return new ReactorClientHttpConnector(httpClient);
    }
}
