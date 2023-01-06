package com.prototype.sessionredis.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.prototype.sessionredis.data.LoginUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ViewController {
    public ViewController() {
        log.info("Controller " + this.getClass().getName() + " has been created.");
    }
    
    @GetMapping("/main")
    public String mainPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("entered main page. user: {}", loginUser.getMember().getUserId());
        
        model.addAttribute("user", loginUser.getMember());
        return "main";
    }
}
