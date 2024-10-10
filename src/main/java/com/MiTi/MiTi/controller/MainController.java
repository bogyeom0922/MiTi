package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.OAuth2.CustomOAuth2User;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.PlaylistService;
import com.MiTi.MiTi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // @Controller로 변경
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MainController {

    private final UserService userService;
    private final AlbumRepository albumRepository;
    private final PlaylistService playlistService;
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    public MainController(UserService userService, AlbumRepository albumRepository, PlaylistService playlistService) {
        this.userService = userService;
        this.albumRepository = albumRepository;
        this.playlistService = playlistService;
    }

    @GetMapping("/main")
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String providerId = "";

        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            providerId = customOAuth2User.getUser().getId().getProviderId();
        }

        Optional<UserDTO> userDTOOpt = userService.getUserById(providerId);
        if (userDTOOpt.isPresent()) {
            model.addAttribute("user", userDTOOpt.get());
        } else {
            // 유저가 없을 경우 처리 (예: 에러 페이지로 리다이렉트)
            return "error";
        }

        // 인기 앨범 목록 가져오기
        List<Album> popularAlbums = albumRepository.findAllByOrderByMusic_popularityDesc();
        model.addAttribute("popularAlbums", popularAlbums);

        // 유저의 선호 장르에 따른 추천 앨범 가져오기
        Map<String, List<Album>> recommendedAlbumsMap = playlistService.getRecommendedAlbumsByUserGenres(providerId);
        model.addAttribute("recommendedAlbumsMap", recommendedAlbumsMap);

        // 맞춤형 추천 앨범 추가
        List<Album> customizedAlbums = playlistService.getCustomizedAlbumsByUser(providerId);
        model.addAttribute("customizedAlbums", customizedAlbums);

        return "main"; // main.html을 반환
    }

    @GetMapping("/api/main") // API 엔드포인트
    @ResponseBody
    public Map<String, Object> getMainData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String providerId = "";

        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            providerId = customOAuth2User.getUser().getId().getProviderId();
        }

        Optional<UserDTO> userDTOOpt = userService.getUserById(providerId);
        if (!userDTOOpt.isPresent()) {
            // 유저가 없을 경우 처리
            return Collections.singletonMap("status", "error");
        }

        UserDTO userDTO = userDTOOpt.get();
        List<Album> popularAlbums = albumRepository.findAllByOrderByMusic_popularityDesc();
        Map<String, List<Album>> recommendedAlbumsMap = playlistService.getRecommendedAlbumsByUserGenres(providerId);
        List<Album> customizedAlbums = playlistService.getCustomizedAlbumsByUser(providerId);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("user", userDTO);
        response.put("popularAlbums", popularAlbums);
        response.put("recommendedAlbumsMap", recommendedAlbumsMap);
        response.put("customizedAlbums", customizedAlbums);

        return response; // JSON 응답 반환
    }
}