package com.MiTi.MiTi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "your-email@example.com";  // 실제 이메일 주소로 변경
    private String verificationCode;  // 인증 코드 저장

    // 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }

    // 이메일 양식 작성
    private MimeMessage createMail(String mail) {
        verificationCode = generateVerificationCode();  // 인증 코드 생성
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);  // 보내는 이메일
            message.setRecipients(MimeMessage.RecipientType.TO, mail); // 받는 이메일 설정
            message.setSubject("[MiTi] 회원가입을 위한 이메일 인증");  // 제목 설정

            String body = "<h1>안녕하세요.</h1>";
            body += "<h1>MiTiʚ♡ɞ  입니다.</h1>";
            body += "<h3>회원가입을 위한 요청하신 인증 번호입니다.</h3><br>";
            body += "<h2>아래 코드를 회원가입 창으로 돌아가 입력해주세요.</h2>";

            body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
            body += "<h2>회원가입 인증 코드입니다.</h2>";
            body += "<h1 style='color:blue'>" + verificationCode + "</h1>";
            body += "</div><br>";
            body += "<h3>감사합니다.</h3>";

            message.setContent(body, "text/html; charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace(); // 로깅을 고려하세요
        }

        return message;
    }

    // 이메일 발송
    public String sendEmail(String userEmail) {
        MimeMessage message = createMail(userEmail);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // 로깅을 고려하세요
            return null;
        }
        return verificationCode;
    }

    // 인증 코드 확인
    public boolean verifyCode(String inputCode) {
        return verificationCode != null && verificationCode.equals(inputCode);
    }

    // sendVerificationCode 메서드 정의
    public String sendVerificationCode(String email) {
        return sendEmail(email);
    }
}
