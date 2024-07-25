package com.MiTi.MiTi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "your-email@example.com";  // 실제 이메일 주소로 변경
    private final Map<String, String> verificationCodes = new HashMap<>();  // 인증 코드를 저장하는 맵

    // 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }

    // 인증 코드 이메일 양식 작성
    private MimeMessage createVerificationMail(String mail) {
        String verificationCode = generateVerificationCode();  // 인증 코드 생성
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);  // 보내는 이메일
            helper.setTo(mail); // 받는 이메일 설정
            helper.setSubject("[MiTi] 회원가입을 위한 이메일 인증");  // 제목 설정
            helper.setText(generateVerificationEmailBody(verificationCode), true);  // 내용 설정
        } catch (MessagingException e) {
            logger.error("Failed to create email", e);
        }

        verificationCodes.put(mail, verificationCode);  // 인증 코드 저장

        return message;
    }

    private String generateVerificationEmailBody(String code) {
        String body = "<h1>안녕하세요.</h1>";
        body += "<h1>MiTiʚ♡ɞ  입니다.</h1>";
        body += "<h3>회원가입을 위한 요청하신 인증 번호입니다.</h3><br>";
        body += "<h2>아래 코드를 회원가입 창으로 돌아가 입력해주세요.</h2>";
        body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
        body += "<h2>회원가입 인증 코드입니다.</h2>";
        body += "<h1 style='color:blue'>" + code + "</h1>";
        body += "</div><br>";
        body += "<h3>감사합니다.</h3>";
        return body;
    }

    // 이메일 발송
    public String sendEmail(String to, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);  // 보내는 이메일
            helper.setTo(to); // 받는 이메일 설정
            helper.setSubject(subject);  // 제목 설정
            helper.setText(text, true);  // 내용 설정
        } catch (MessagingException e) {
            logger.error("Failed to create email", e);
            return null;
        }

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            return null;
        }
        return verificationCodes.get(to);  // 인증 코드 반환
    }

    // 인증 코드 확인
    public boolean verifyCode(String email, String inputCode) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(inputCode);
    }

    // sendVerificationCode 메서드 정의
    public String sendVerificationCode(String email) {
        String code = generateVerificationCode();
        String body = generateVerificationEmailBody(code);
        sendEmail(email, "[MiTi] 회원가입을 위한 이메일 인증", body);
        verificationCodes.put(email, code);  // 인증 코드 저장
        return code;
    }

    // 비밀번호 전송 메서드
    public void sendPasswordEmail(String email, String password) {
        String subject = "[MiTi] 비밀번호 찾기";
        String body = "<h1>안녕하세요.</h1>";
        body += "<h1>MiTiʚ♡ɞ  입니다.</h1>";
        body += "<h3>요청하신 비밀번호는 아래와 같습니다.</h3><br>";
        body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
        body += "<h2>비밀번호:</h2>";
        body += "<h1 style='color:blue'>" + password + "</h1>";
        body += "</div><br>";
        body += "<h3>감사합니다.</h3>";

        sendEmail(email, subject, body);
    }
}
