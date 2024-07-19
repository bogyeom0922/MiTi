package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/save")
    public String saveForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO, Model model) {
        // 이메일 중복 확인은 Service에서 처리하므로 별도의 처리 없이 DTO를 Service로 전달합니다.
        String result = memberService.registerMember(memberDTO);
        if ("duplicate".equals(result)) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "save";
        }
        // 회원가입 성공 시 홈페이지로 리다이렉트
        return "redirect:/";
    }

    @PostMapping("/email-check")
    @ResponseBody
    public String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        String result = memberService.emailCheck(memberEmail);
        if ("duplicate".equals(result)) {
            return "duplicate";
        }
        return "ok";
    }

    @PostMapping("/id-check")
    @ResponseBody
    public boolean idCheck(@RequestParam("memberId") String memberId) {
        return memberService.idCheck(memberId);
    }
}