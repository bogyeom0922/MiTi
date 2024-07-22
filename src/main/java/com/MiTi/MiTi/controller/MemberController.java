package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.service.MemberService;
import com.MiTi.MiTi.service.EmailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService; // emailService 필드 추가

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html을 반환
    }

    @GetMapping("/save")
    public String saveForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO, Model model) {
        // 회원가입 처리
        String result = memberService.registerMember(memberDTO);
        if ("duplicate".equals(result)) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "save";
        }
        // 회원가입 성공 시 홈페이지로 리다이렉트
        return "redirect:/";
    }

    @PostMapping("/id-check")
    @ResponseBody
    public boolean idCheck(@RequestParam("memberId") String memberId) {
        System.out.println("Received memberId: " + memberId);
        return memberService.idCheck(memberId);
    }

    @PostMapping("/send-email")
    @ResponseBody
    public ResponseEntity<Void> sendEmail(@RequestParam("email") String email, HttpSession session) {
        // 이메일 전송 및 인증번호 저장
        String verificationCode = emailService.sendVerificationCode(email);
        session.setAttribute("verificationCode", verificationCode); // 세션에 전송된 인증번호를 저장.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    @ResponseBody
    public ResponseEntity<Boolean> verifyEmail(@RequestParam("code") String code, HttpSession session) {
        String storedCode = (String) session.getAttribute("verificationCode");

        if (storedCode != null && storedCode.equals(code)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }
}
