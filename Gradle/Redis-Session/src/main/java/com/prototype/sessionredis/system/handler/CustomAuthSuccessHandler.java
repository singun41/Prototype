package com.prototype.sessionredis.system.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.prototype.sessionredis.data.LoginUser;
import com.prototype.sessionredis.vo.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    public CustomAuthSuccessHandler() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        String ipAddr = req.getHeader("X-FORWARDED-FOR");
        
        if(ipAddr == null || ipAddr.isBlank())
            ipAddr = req.getHeader("Proxy-Client-IP");
        if(ipAddr == null || ipAddr.isBlank())
            ipAddr = req.getRemoteAddr();


        String browser = null;
        String userAgent = req.getHeader(Constants.USER_AGENT);

        if(userAgent.contains("Trident"))   // IE
            browser = "IE";

        else if(userAgent.contains("Edg"))   // Edge
            browser = "Edge";

        else if(userAgent.contains("Whale"))   // Naver Whale
            browser = "Naver Whale";

        else if(userAgent.contains("Opera") || userAgent.contains("OPR"))   // Opera
            browser = "Opera";

        else if(userAgent.contains("Firefox"))   // Firefox
            browser = "Firefox";

        else if(userAgent.contains("Chrome"))   // Chrome
            browser = "Chrome";

        else if(userAgent.contains("Safari"))   // Safari
            browser = "Safari";

        else
            browser = "Others";
        
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();

        log.info("Logged in user: {}", loginUser.getMember().getId());
        log.info("Host ip: {}, Browser: {}", ipAddr, browser);
        log.info("User-Agent: {}", userAgent);
        
        res.sendRedirect(Constants.URL_MAIN);   // ViewController
    }
}
