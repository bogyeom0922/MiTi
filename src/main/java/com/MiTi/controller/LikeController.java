
package com.MiTi.controller;


import com.MiTi.dto.LikeDto;
import com.MiTi.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/mypage/like")
    public String addLike(@RequestBody LikeDto likeDto) {
        likeService.addLike(likeDto);
        return "redirect:/mypage/like";
    }

    @DeleteMapping("/deleteLike")
    public ResponseEntity<String> deleteLike(@RequestParam("userId") Integer userId, @RequestParam("albumId") Integer albumId) {
        try {
            likeService.deleteLike(userId, albumId);
            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting like: " + e.getMessage());
        }
    }
}
