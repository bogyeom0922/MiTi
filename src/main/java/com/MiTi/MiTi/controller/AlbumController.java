package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AlbumController {

    @Autowired
    private AlbumService albumService;
    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/main_list")
    public String mainList(Model model) {
        List<String> details = albumRepository.findAll()
                .stream()
                .map(Album::getDetail)
                .toList();

        // 중복을 제거하기 위해 Set으로 변환 후 다시 리스트로 변환
        List<String> uniqueDetails = details.stream()
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("details", uniqueDetails);
        return "album/main_list";
    }

    @GetMapping("/detail/{detail}")
    public String detailList(@PathVariable("detail") String detail, Model model) {
        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);
        return "album/detail";
    }

}