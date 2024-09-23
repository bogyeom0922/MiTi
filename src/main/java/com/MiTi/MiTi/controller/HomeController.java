package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.OAuth2.CustomOAuth2User;
import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.LikeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final AlbumService albumService;

    public HomeController(AlbumService albumService) {
        this.albumService = albumService;
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
    public String indexPage(@RequestParam(defaultValue = "0") int page, Model model) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ""; // 기본값은 빈 문자열로 초기화
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            userId = customOAuth2User.getUser().getId().getProviderId();  // 혹은 적절한 UserId 필드 사용
        }

        // 페이지네이션을 위한 Pageable 객체 생성
        int pageSize = 6;  // 한 페이지에 보여줄 앨범 수
        Pageable pageable = PageRequest.of(page, pageSize);

        // userId를 이용하여 albumList 가져오기 (pageable 적용)
        Page<AlbumDto> albumPage = albumService.getAlbumList(pageable);  // getAlbumList에 Pageable 전달
        List<AlbumDto> albumDtoList = albumPage.getContent();

        // 모델에 추가
        model.addAttribute("albumList", albumDtoList);
        model.addAttribute("userId", userId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", albumPage.getTotalPages());

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


