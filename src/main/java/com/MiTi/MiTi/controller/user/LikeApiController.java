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
            @PathVariable Long musicId,  // Long 타입으로 musicId를 받음
            @RequestParam String username) {
        // musicId(Long)를 String으로 변환
        boolean success = likeService.addLike(username, String.valueOf(musicId));
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{musicId}")
    public ResponseEntity<Map<String, Object>> unlikeTrack(
            @PathVariable Long musicId,  // Long 타입으로 musicId를 받음
            @RequestParam String username) {
        // 좋아요 취소 처리 (String으로 변환된 musicId 사용)
        likeService.deleteLike(musicId);  // 이 메서드에서 Long 타입을 받아도 괜찮다면 그대로 둡니다
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}