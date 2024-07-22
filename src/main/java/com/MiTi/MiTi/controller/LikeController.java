package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.service.LikeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/mypage/like")
    public String list(Model model) {
        List<LikeDto> likeDtoList = likeService.getLikeList();
        model.addAttribute("postList", likeDtoList);
        return "mypage_like";
    }
}
