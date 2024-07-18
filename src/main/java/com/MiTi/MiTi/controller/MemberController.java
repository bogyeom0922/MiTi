package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/save")
    public String saveForm() {
        return "save"; // 회원가입 페이지 뷰 반환
    }

    @PostMapping("/save")
    public String saveMember(@ModelAttribute MemberDTO memberDTO, Model model) {
        // 이메일 중복 체크
        String checkResult = memberService.emailCheck(memberDTO.getMemberEmail());
        if ("duplicate".equals(checkResult)) {
            // 중복된 이메일인 경우 팝업으로 메시지 반환
            model.addAttribute("duplicateEmail", memberDTO.getMemberEmail());
            return "save"; // 다시 회원가입 페이지로 이동
        }

        // 중복되지 않은 경우 회원 등록
        String result = memberService.registerMember(memberDTO);
        if ("ok".equals(result)) {
            return "redirect:/"; // 회원가입 성공 시 메인 페이지로 리다이렉트
        } else {
            // 회원 등록 실패 처리
            model.addAttribute("errorMessage", "회원 등록에 실패하였습니다.");
            return "errorPage";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // 로그인 페이지 뷰 반환
    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberDTO memberDTO, Model model, HttpSession session) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            // 로그인 성공 시 세션에 이메일 저장하고 메인 페이지로 리다이렉트
            session.setAttribute("loginEmail", loginResult.getMemberEmail());
            return "redirect:/"; // 메인 페이지로 리다이렉트
        } else {
            // 로그인 실패 시 다시 로그인 페이지로
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "login";
        }
    }

    @GetMapping("/")
    public String findAll(Model model) {
        // 모든 회원 목록 조회
        model.addAttribute("memberList", memberService.findAll());
        return "list"; // 회원 목록 페이지 뷰 반환
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        // 특정 회원 정보 조회
        MemberDTO memberDTO = memberService.findById(id);
        if (memberDTO == null) {
            // 회원이 없을 경우 에러 페이지로
            return "errorPage";
        }
        model.addAttribute("member", memberDTO);
        return "detail"; // 회원 상세 페이지 뷰 반환
    }

    @GetMapping("/update")
    public String updateForm(HttpSession session, Model model) {
        // 회원 정보 수정 페이지 출력 요청
        String myEmail = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        if (memberDTO == null) {
            // 회원이 없을 경우 에러 페이지로
            return "errorPage";
        }
        model.addAttribute("updateMember", memberDTO);
        return "update"; // 회원 정보 수정 페이지 뷰 반환
    }

    @PostMapping("/update")
    public String updateMember(@ModelAttribute MemberDTO memberDTO) {
        // 회원 정보 업데이트 처리
        memberService.update(memberDTO);
        return "redirect:/" + memberDTO.getId(); // 수정 후 해당 회원의 상세 페이지로 리다이렉트
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        // 회원 삭제 처리
        memberService.deleteById(id);
        return "redirect:/"; // 삭제 후 메인 페이지로 리다이렉트
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 로그아웃 처리
        session.invalidate(); // 세션 무효화
        return "redirect:/"; // 메인 페이지로 리다이렉트
    }

    @PostMapping("/email-check")
    @ResponseBody
    public String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        // 이메일 중복 체크 (AJAX 요청)
        return memberService.emailCheck(memberEmail);
    }
}
