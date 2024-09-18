package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.service.LikeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    private final LikeService likeService;

    public HomeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            return "redirect:/login";  // 토큰이 없으면 로그인 페이지로 리디렉트
        }

        // 토큰이 있으면 홈 페이지로 이동
        return "home";  // home.html로 이동
    }

    @GetMapping("/index")
    public String indexPage(Model model) {
        List<LikeDto> likeDtoList = likeService.getLikeListByUserId("1"); // 적절한 userId로 설정
        model.addAttribute("likeList", likeDtoList); // likeList를 모델에 추가
        return "index";
    }


    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
