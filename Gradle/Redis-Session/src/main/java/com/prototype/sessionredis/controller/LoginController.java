package com.prototype.sessionredis.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prototype.sessionredis.data.LoginUser;
import com.prototype.sessionredis.vo.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
    @Autowired
    public LoginController() {
        log.info("Controller " + this.getClass().getName() + " has been created.");
    }


    @RequestMapping(Constants.URL_LOGIN)   // 로그인 실패시 메시지를 전달하려면 GetMapping 대신 RequestMapping으로 설정해야 한다.
    public String login(HttpServletRequest req, @AuthenticationPrincipal LoginUser loginUser) {
        boolean loggedIn = loginUser != null ? true : false;

        if(loggedIn)
            return "redirect:main";
        else
            return "login";
    }
}
