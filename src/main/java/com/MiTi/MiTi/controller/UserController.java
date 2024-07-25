package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.EmailService;
import com.MiTi.MiTi.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html 반환
    }

    @GetMapping("/save")
    public String saveForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute UserDTO userDTO, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        String userId = (String) session.getAttribute("userId");
        String verificationStatus = (String) session.getAttribute("verificationStatus");
        String idStatus = (String) session.getAttribute("idStatus");

        if (email == null || userId == null || !"verified".equals(verificationStatus) || !"ok".equals(idStatus)) {
            model.addAttribute("error", "이메일 인증 또는 아이디 중복 확인을 완료해주세요.");
            return "save";
        }

        userDTO.setUserMail(email);
        userDTO.setUserId(userId);

        String result = userService.registerUser(userDTO);

        if ("duplicate_email".equals(result)) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "save";
        }
        if ("duplicate_id".equals(result)) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "save";
        }

        userService.saveUser(userDTO); // 유저 저장
        session.removeAttribute("verificationCode");
        session.removeAttribute("email");
        session.removeAttribute("userId");
        session.removeAttribute("verificationStatus");
        session.removeAttribute("idStatus");

        model.addAttribute("success", "회원가입되었습니다."); // 성공 메시지 추가
        return "index"; // 회원가입 성공 시 index 페이지로 이동
    }

    @PostMapping("/email-check")
    @ResponseBody
    public String emailCheck(@RequestParam("userMail") String userMail) {
        boolean isDuplicate = userService.emailCheck(userMail);
        if (isDuplicate) {
            return "duplicate";
        }
        return "ok";
    }

    @PostMapping("/id-check")
    @ResponseBody
    public String idCheck(@RequestParam("userId") String userId, HttpSession session) {
        boolean isDuplicate = userService.idCheck(userId);
        if (!isDuplicate) {
            session.setAttribute("userId", userId); // 세션에 유저 아이디 저장
            session.setAttribute("idStatus", "ok");
        }
        return isDuplicate ? "duplicate" : "ok";
    }

    @PostMapping("/send-email")
    @ResponseBody
    public ResponseEntity<Void> sendEmail(@RequestParam("email") String email, HttpSession session) {
        String fullEmail = email; // 전체 이메일 주소로 중복 체크
        if ("duplicate".equals(emailCheck(fullEmail))) {
            return ResponseEntity.status(400).build();
        }
        String verificationCode = emailService.sendVerificationCode(fullEmail);
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("email", fullEmail);
        session.setAttribute("verificationStatus", "pending");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    @ResponseBody
    public ResponseEntity<Boolean> verifyEmail(@RequestParam("code") String code, HttpSession session) {
        String storedCode = (String) session.getAttribute("verificationCode");

        if (storedCode != null && storedCode.equals(code)) {
            session.setAttribute("verificationStatus", "verified");
            return ResponseEntity.ok(true);
        } else {
            session.setAttribute("verificationStatus", "pending");
            return ResponseEntity.ok(false);
        }
    }
    @GetMapping("/findid")
    public String findIdForm() {
        return "findid"; // findpw.html 반환
    }
    @PostMapping("/findid")
    public String findIdByEmail(@RequestParam("email") String email, Model model) {
        String userId = userService.findUserIdByEmail(email);
        if (userId != null) {
            model.addAttribute("message", "아이디는 " + userId + " 입니다.");
        } else {
            model.addAttribute("message", "해당 이메일로 등록된 아이디가 없습니다.");
        }
        return "findid"; // 같은 findid.html 페이지로 반환
    }

    @GetMapping("/findpw")
    public String findPwForm() {
        return "findpw"; // findpw.html 반환
    }

    @PostMapping("/findpw")
    public String findPwByEmail(@RequestParam("email") String email, Model model) {
        boolean isEmailSent = userService.sendPasswordByEmail(email);
        if (isEmailSent) {
            model.addAttribute("message", "비밀번호가 이메일로 전송되었습니다.");
        } else {
            model.addAttribute("message", "해당 이메일로 등록된 계정이 없습니다.");
        }
        return "findpw"; // 같은 findpw.html 페이지로 반환
    }
}
