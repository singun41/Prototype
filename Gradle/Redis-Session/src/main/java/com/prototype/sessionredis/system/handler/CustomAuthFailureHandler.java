package com.prototype.sessionredis.system.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.prototype.sessionredis.vo.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
	@Autowired
	public CustomAuthFailureHandler() {
		log.info("Component '" + this.getClass().getName() + "' has been created.");
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException authEx) throws IOException, ServletException {
		StringBuilder errorMsg = new StringBuilder();
		String ipAddr = req.getHeader("X-FORWARDED-FOR");

		if(ipAddr == null || ipAddr.isBlank())
            ipAddr = req.getHeader("Proxy-Client-IP");
		if(ipAddr == null || ipAddr.isBlank())
			ipAddr = req.getRemoteAddr();


		// 패스워드가 틀렸다는 메시지를 따로 전달하게 되면 ID가 일치한다는 것을 명시하는 것이므로, ID나 패스워드가 틀렸다는 통합 문구로 안내한다.
		if(authEx instanceof BadCredentialsException || authEx instanceof InternalAuthenticationServiceException) {
			errorMsg.append("로그인 실패");
			log.warn("User login failed. ID or password mismatched. host ip: {}", ipAddr);

		} else if(authEx instanceof DisabledException) {
			errorMsg.append("사용 중지된 ID 입니다.");
			log.warn("User's access is denied. login failed. host ip: {}", ipAddr);

		} else {
			errorMsg.append("시스템 에러입니다.");
			log.warn("Login error. host ip: {}", ipAddr);
		}
		
		req.setAttribute("errorMsg", errorMsg.toString());
		req.getRequestDispatcher(Constants.URL_LOGIN).forward(req, res);   // SystemController
	}
}
