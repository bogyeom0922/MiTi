package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.service.EmailService;
import com.MiTi.MiTi.service.MemberService;
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
    private final EmailService emailService;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html 반환
    }

    @GetMapping("/save")
    public String saveForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO, Model model) {
        String result = memberService.registerMember(memberDTO);
        if ("duplicate_email".equals(result)) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "save";
        }
        if ("duplicate_id".equals(result)) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "save";
        }
        return "redirect:/";
    }

    @PostMapping("/email-check")
    @ResponseBody
    public String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        boolean isDuplicate = memberService.emailCheck(memberEmail);
        if (isDuplicate) {
            return "duplicate";
        }
        return "ok";
    }

    @PostMapping("/id-check")
    @ResponseBody
    public boolean idCheck(@RequestParam("memberId") String memberId) {
        return memberService.idCheck(memberId);
    }

    @PostMapping("/send-email")
    @ResponseBody
    public ResponseEntity<Void> sendEmail(@RequestParam("email") String email, HttpSession session) {
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
