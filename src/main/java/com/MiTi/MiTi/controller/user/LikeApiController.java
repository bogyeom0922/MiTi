package com.MiTi.MiTi.controller.user;

import com.MiTi.MiTi.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/like")
public class LikeApiController {

    private final LikeService likeService;

    public LikeApiController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{musicId}")
    public ResponseEntity<Map<String, Object>> likeTrack(
            @PathVariable Long musicId,
            @RequestParam String username) {
        // 사용자 이름과 음악 ID를 사용해 좋아요 처리
        boolean success = likeService.addLike(username, musicId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{musicId}")
    public ResponseEntity<Map<String, Object>> unlikeTrack(
            @PathVariable Long musicId,
            @RequestParam String username) {
        // 사용자 이름과 음악 ID를 사용해 좋아요 취소 처리
        likeService.deleteLike(musicId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
