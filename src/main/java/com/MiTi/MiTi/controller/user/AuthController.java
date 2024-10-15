package com.MiTi.MiTi.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class AuthController {

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 현재 세션을 가져온다. 세션이 존재하지 않으면 null 반환.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ResponseEntity.ok("로그아웃 성공");
    }
}