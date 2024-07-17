package com.MiTi.controller;

import com.MiTi.dto.SigninDto;
import com.MiTi.service.SigninService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SigninController {

    private final SigninService signinService;

    @Autowired
    public SigninController(SigninService signinService) {
        this.signinService = signinService;
    }

    @GetMapping("/signin")
    public String getSigninForm() {
        return "signin";
    }

    @PostMapping("/post")
    public String write(SigninDto signinDto) {
        signinService.savePost(signinDto);
        return "redirect:/signin";
    }
}
