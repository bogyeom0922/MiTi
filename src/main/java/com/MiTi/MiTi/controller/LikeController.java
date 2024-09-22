package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.LikeRepository;
import com.MiTi.MiTi.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        List<LikeDto> likeDtoList = likeService.getLikeListByUserId(String.valueOf(userId));
        model.addAttribute("likeList", likeDtoList);
        model.addAttribute("userId", userId);
        return "mypage_like";
    }

    // 게시글 삭제 (물리적 삭제)

    @DeleteMapping("/mypage/like/{id}")
    @ResponseBody
    public String deleteLike(@PathVariable Long id) {
        try {
            likeService.deleteLike(id);
            return "success";
        } catch (Exception e) {
            return "failure";
        }
    }





}
