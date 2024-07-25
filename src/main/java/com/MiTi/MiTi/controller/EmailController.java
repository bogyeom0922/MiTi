package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.JoinRequest;
import com.MiTi.MiTi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/emailConfirm")
    public String mailConfirm(@RequestBody JoinRequest joinRequest, HttpSession session) {
        String authCode = emailService.sendVerificationCode(joinRequest.getEmail());
        session.setAttribute("email", joinRequest.getEmail());
        session.setAttribute("authCode", authCode);
        return "인증 코드가 발급되었습니다: " + authCode;
    }

    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam(name = "authCode") String authCode, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "인증 요청을 다시 시도해 주세요.";
        }

        boolean isValid = emailService.verifyCode(email, authCode);
        if (isValid) {
            session.setAttribute("verificationStatus", "verified");
            return "인증 코드가 확인되었습니다. 회원 가입이 완료되었습니다.";
        } else {
            session.setAttribute("verificationStatus", "pending");
            return "인증 코드가 올바르지 않거나 만료되었습니다. 다시 시도해 주세요.";
        }
    }
}
