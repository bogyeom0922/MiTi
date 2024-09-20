package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.OAuth2.CustomOAuth2User;
import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.service.LikeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
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
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ""; // 기본값은 빈 문자열로 초기화
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            userId = customOAuth2User.getUser().getId().getProviderId();  // 혹은 적절한 UserId 필드 사용
        }

        // userId를 이용하여 likeList 가져오기
        List<LikeDto> likeDtoList = likeService.getLikeListByUserId(userId);
        model.addAttribute("likeList", likeDtoList); // likeList를 모델에 추가
        model.addAttribute("userId", userId); // userId를 모델에 추가
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


