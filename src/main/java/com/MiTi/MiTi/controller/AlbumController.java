package com.MiTi.MiTi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AlbumController {
    @Autowired
    private AlbumRepository albumRepository;
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/album/{id}")
    public String album_detail(@PathVariable("id") Long id, Model model) {
        // 1. id를 조회해 데이터 가져오기
        Album album = albumRepository.findById(id).orElse(null);
        // 2. 모델에 데이터 등록하기
        model.addAttribute("album", album);
        return "album/album_detail";
    }

}
