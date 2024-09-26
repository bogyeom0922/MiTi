package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.PlaylistService;
import com.MiTi.MiTi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(MainController.class); // Logger 추가

    @Autowired
    public MainController(UserService userService, AlbumRepository albumRepository, PlaylistService playlistService) {
        this.userService = userService;
        this.albumRepository = albumRepository;
        this.playlistService = playlistService;
    }

    @GetMapping("/main/{providerId}")
    public String mainPage(@PathVariable("providerId") String providerId, Model model) {
        System.out.println("Provider ID in MainController: " + providerId);
        // providerId로 사용자 정보를 가져와 모델에 추가
        Optional<UserDTO> userDTOOpt = userService.getUserById(providerId);
        log.debug("Fetched UserDTO: " + userDTOOpt.orElse(null));

        if (userDTOOpt.isPresent()) {
            model.addAttribute("user", userDTOOpt.get());
            log.info("User found: " + userDTOOpt.get().getName());
        } else {
            log.warn("User not found for providerId: " + providerId);
            return "error";
        }

        // 인기 순위에 따라 정렬된 앨범 목록 가져오기
        List<Album> popularAlbums = albumRepository.findAllByOrderByMusic_popularityDesc();
        model.addAttribute("albums", popularAlbums);

        // 유저의 선호 장르에 따른 추천 앨범 가져오기
        Map<String, List<Album>> recommendedAlbumsMap = playlistService.getRecommendedAlbumsByUserGenres(providerId);
        model.addAttribute("recommendedAlbumsMap", recommendedAlbumsMap);

        return "main"; // main.html 템플릿 반환
    }
}
