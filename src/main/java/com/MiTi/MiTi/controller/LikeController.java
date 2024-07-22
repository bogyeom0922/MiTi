package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.LikeRepository;
import com.MiTi.MiTi.service.LikeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LikeController {

    private final LikeService likeService;
    private final LikeRepository likeRepository;

    public LikeController(LikeService likeService, LikeRepository likeRepository) {
        this.likeService = likeService;
        this.likeRepository = likeRepository;
    }

    @GetMapping("/mypage/like/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<LikeDto> recordDtoList = likeService.getLikeListByUserId(String.valueOf(userId));
        model.addAttribute("postList", recordDtoList);
        return "mypage_like";
    }





}
