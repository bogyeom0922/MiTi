package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.EmailService;
import com.MiTi.MiTi.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
        return "signup/login"; // login.html 반환
    }

    @GetMapping("/save")
    public String saveForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "signup/save";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> save(@ModelAttribute UserDTO userDTO, HttpSession session) {
        String email = (String) session.getAttribute("email");
        String userId = (String) session.getAttribute("userId");
        String verificationStatus = (String) session.getAttribute("verificationStatus");
        String idStatus = (String) session.getAttribute("idStatus");

        if (email == null || userId == null || !"verified".equals(verificationStatus) || !"ok".equals(idStatus)) {
            return ResponseEntity.badRequest().body("이메일 인증 또는 아이디 중복 확인을 완료해주세요.");
        }

        userDTO.setUserMail(email);
        userDTO.setUserId(userId);

        String result = userService.registerUser(userDTO);

        if ("duplicate_email".equals(result)) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }
        if ("duplicate_id".equals(result)) {
            return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
        }
        if ("duplicate".equals(result)) {
            return ResponseEntity.badRequest().body("중복된 데이터가 있습니다.");
        }
        if ("error".equals(result)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }

        session.removeAttribute("verificationCode");
        session.removeAttribute("email");
        session.removeAttribute("userId");
        session.removeAttribute("verificationStatus");
        session.removeAttribute("idStatus");

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
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
        String email = (String) session.getAttribute("email");

        if (email != null && emailService.verifyCode(email, code)) {
            session.setAttribute("verificationStatus", "verified");
            return ResponseEntity.ok(true);
        } else {
            session.setAttribute("verificationStatus", "pending");
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/findid")
    public String findIdForm() {
        return "signup/findid"; // findid.html 반환
    }

    @PostMapping("/findid")
    public String findIdByEmail(@RequestParam("email") String email, Model model) {
        String userId = userService.findUserIdByEmail(email);
        if (userId != null) {
            model.addAttribute("message", "아이디는 " + userId + " 입니다.");
        } else {
            model.addAttribute("message", "해당 이메일로 등록된 아이디가 없습니다.");
        }
        return "signup/findid"; // 같은 findid.html 페이지로 반환
    }

    @GetMapping("/findpw")
    public String findPwForm() {
        return "signup/findpw"; // findpw.html 반환
    }

    @PostMapping("/findpw")
    public String findPwByEmail(@RequestParam("email") String email, Model model) {
        boolean isEmailSent = userService.sendPasswordByEmail(email);
        if (isEmailSent) {
            model.addAttribute("message", "비밀번호가 이메일로 전송되었습니다.");
        } else {
            model.addAttribute("message", "해당 이메일로 등록된 계정이 없습니다.");
        }
        return "signup/findpw"; // 같은 findpw.html 페이지로 반환
    }
}
