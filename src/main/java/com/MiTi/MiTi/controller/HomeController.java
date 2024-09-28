package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.OAuth2.CustomOAuth2User;
import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.LikeService;
import com.MiTi.MiTi.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserService userService;
    private final AlbumService albumService;

    public HomeController(AlbumService albumService, UserService userService) {
        this.userService = userService;
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

    @GetMapping("/index/{providerId}")
    public String indexPage(@PathVariable("providerId") String providerId, @RequestParam(defaultValue = "0") int page, Model model) {

        // provider와 providerId로 사용자 정보를 가져와 모델에 추가
        Optional<UserDTO> userDTOOpt = userService.getUserById(providerId);

        if (userDTOOpt.isPresent()) {
            model.addAttribute("user", userDTOOpt.get());
        } else {
            // 유저가 없을 경우 처리 (예: 에러 페이지로 리다이렉트)
            return "error";
        }

        // 페이지네이션을 위한 Pageable 객체 생성
        int pageSize = 6;  // 한 페이지에 보여줄 앨범 수
        Pageable pageable = PageRequest.of(page, pageSize);

        // userId를 이용하여 albumList 가져오기 (pageable 적용)
        Page<AlbumDto> albumPage = albumService.getAlbumList(pageable);  // getAlbumList에 Pageable 전달
        List<AlbumDto> albumDtoList = albumPage.getContent();

        // 모델에 추가
        model.addAttribute("albumList", albumDtoList);
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


