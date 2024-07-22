package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.JoinRequest;
import com.MiTi.MiTi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/emailConfirm")
    public String mailConfirm(@RequestBody JoinRequest joinRequest) {
        String authCode = emailService.sendEmail(joinRequest.getEmail());
        return "인증 코드가 발급되었습니다: " + authCode;
    }

    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam(name = "authCode") String authCode) {
        boolean isValid = emailService.verifyCode(authCode);
        if (isValid) {
            return "인증 코드가 확인되었습니다. 회원 가입이 완료되었습니다.";
        } else {
            return "인증 코드가 올바르지 않거나 만료되었습니다. 다시 시도해 주세요.";
        }
    }
}