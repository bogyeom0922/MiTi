package com.MiTi.MiTi.controller.user;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie; // jakarta로 변경
import jakarta.servlet.http.HttpServletRequest; // jakarta로 변경
import jakarta.servlet.http.HttpServletResponse; // jakarta로 변경

@RestController
public class AuthController {

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Spring Security의 로그아웃 처리
        new SecurityContextLogoutHandler().logout(request, response, null);

        // JSESSIONID 쿠키 삭제
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", null);
        jsessionIdCookie.setPath("/"); // 경로를 설정
        jsessionIdCookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(jsessionIdCookie);

        // Spotify 쿠키 삭제
        Cookie spDcCookie = new Cookie("sp_dc", null);
        spDcCookie.setPath("/"); // 경로를 설정
        spDcCookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(spDcCookie);

        Cookie spKeyCookie = new Cookie("sp_key", null);
        spKeyCookie.setPath("/"); // 경로를 설정
        spKeyCookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(spKeyCookie);

        return "redirect:/"; // 로그아웃 후 홈으로 리다이렉트
    }
}