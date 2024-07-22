package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.entity.MemberEntity;
import com.MiTi.MiTi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    public boolean idCheck(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    private String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return Integer.toString(code);
    }

    private void saveAuthCode(String email, String authCode) {
        if (email == null || email.isEmpty()) {
            logger.error("Cannot save auth code because email is null or empty");
            return;
        }

        Optional<MemberEntity> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            member.setAuthCode(authCode);
            member.setAuthCodeExpiration(LocalDateTime.now().plusMinutes(10));
            memberRepository.save(member);
        } else {
            logger.error("No member found with email: {}", email);
        }
    }

    private void sendAuthCodeEmail(String email, String authCode) {
        if (email == null || email.isEmpty()) {
            logger.error("Cannot send email because email is null or empty");
            return;
        }

        String subject = "회원가입 인증 코드";
        String body = "회원가입을 위해 아래의 인증 코드를 입력하세요:\n\n" + authCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public String registerMember(MemberDTO memberDTO) {
        if (memberDTO == null) {
            logger.error("MemberDTO is null");
            return "error";
        }

        if (idCheck(memberDTO.getMemberId())) {
            return "duplicate";
        }

        String authCode = generateAuthCode();
        saveAuthCode(memberDTO.getMemberEmail(), authCode);
        sendAuthCodeEmail(memberDTO.getMemberEmail(), authCode);

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);

        return "ok";
    }

    public boolean verifyCode(String email, String providedCode) {
        if (email == null || email.isEmpty()) {
            logger.error("Cannot verify code because email is null or empty");
            return false;
        }

        Optional<MemberEntity> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            String storedCode = member.getAuthCode();
            LocalDateTime expiration = member.getAuthCodeExpiration();

            return storedCode != null && storedCode.equals(providedCode) && LocalDateTime.now().isBefore(expiration);
        }
        return false;
    }
}
