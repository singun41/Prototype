package com.prototype.jwtstudy;

import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class JwtstudyApplicationTests {
	@Autowired private PBEStringCleanablePasswordEncryptor jasyptStringEncryptor;
	@Autowired private PasswordEncoder passwordEncoder;


	@Test
	void contextLoads() {
		String strRaw = "asdf";
		log.info("raw: {}   encrypt: {}", strRaw, jasyptStringEncryptor.encrypt(strRaw));

		log.info("decrypt: {}, raw: {}", "SYWGhrR/Hx0jPABjKfi6Dw==", jasyptStringEncryptor.decrypt("SYWGhrR/Hx0jPABjKfi6Dw=="));

		log.info("bcrypt --> raw=[{}], encrypt=[{}]", strRaw, passwordEncoder.encode(strRaw));
	}
}
