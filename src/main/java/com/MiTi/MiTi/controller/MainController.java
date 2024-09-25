package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.PlaylistService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {

    private final UserService userService;
    private final AlbumRepository albumRepository;
    private final PlaylistService playlistService;

    // userService와 albumRepository를 함께 주입받는 생성자
    @Autowired
    public MainController(UserService userService, AlbumRepository albumRepository,PlaylistService playlistService) {
        this.userService = userService;
        this.albumRepository = albumRepository;
        this.playlistService = playlistService;
    }


    // 사용자 정보가 포함된 메인 페이지 (providerId를 하나로 통합)
    @GetMapping("/main/{providerId}")
    public String mainPage(@PathVariable("providerId") String providerId, Model model) {
        // providerId로 사용자 정보를 가져와 모델에 추가
        Optional<UserDTO> userDTOOpt = userService.getUserById(providerId);

        if (userDTOOpt.isPresent()) {
            model.addAttribute("user", userDTOOpt.get());
        } else {
            // 유저가 없을 경우 처리 (예: 에러 페이지로 리다이렉트)
            return "error";
        }

        // 인기 순위에 따라 정렬된 앨범 목록 가져오기
        List<Album> popularAlbums = albumRepository.findAllByOrderByMusic_popularityDesc();
        model.addAttribute("albums", popularAlbums);

        // 유저의 선호 장르에 따른 추천 앨범 가져오기 (장르별로 Map 형태로 리턴)
        // 유저의 선호 장르에 따른 추천 앨범 가져오기
        Map<String, List<Album>> recommendedAlbumsMap = playlistService.getRecommendedAlbumsByUserGenres(providerId);
        model.addAttribute("recommendedAlbumsMap", recommendedAlbumsMap);

        return "main"; // main.html 템플릿 반환
    }
}
