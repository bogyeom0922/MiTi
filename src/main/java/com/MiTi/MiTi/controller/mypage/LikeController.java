package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.repository.LikeRepository;
import com.MiTi.MiTi.service.LikeService;
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
        model.addAttribute("postList", likeDtoList);
        model.addAttribute("userId", userId);
        return "mypage/mypage_like";
    }

    @PostMapping("/mypage/like/toggle")
    @ResponseBody
    public String toggleLike(@RequestParam("userId") String userId, @RequestParam("albumId") String albumId) {
        boolean isLiked = likeService.toggleLike(userId, albumId);
        return isLiked ? "liked" : "unliked";
    }

    // 게시글 삭제(물리적 삭제)
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

    // 수록곡에 대한 좋아요 토글 기능 추가
    @PostMapping("/mypage/like/track/toggle")
    @ResponseBody
    public String toggleTrackLike(@RequestParam("userId") String userId, @RequestParam("trackId") Long trackId) {
        boolean isLiked = likeService.toggleTrackLike(userId, trackId);
        return isLiked ? "liked" : "unliked";
    }
}
