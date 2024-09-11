package com.MiTi.MiTi.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            return "redirect:/login";  // 토큰이 없으면 로그인 페이지로 리디렉트
        }

        // 토큰이 있으면 홈 페이지로 이동
        return "home";  // home.html로 이동
    }

    @GetMapping("/index")
    public String indexPage() {
        return "index"; // resources/templates/index.html 파일을 반환
    }

    @GetMapping("/")
    public String home() {
        return "login";
    }


    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
