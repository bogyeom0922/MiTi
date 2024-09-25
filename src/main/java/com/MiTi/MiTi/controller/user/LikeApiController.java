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
            @RequestParam(required = false) String username) {
        // userId가 없으면 기본값으로 비회원 사용자 처리
        if (username == null || username.isEmpty()) {
            username = "anonymous";  // 비회원일 때 기본 사용자 ID
        }

        boolean success = likeService.addLike(username, String.valueOf(musicId));
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{musicId}")
    public ResponseEntity<Map<String, Object>> unlikeTrack(
            @PathVariable Long musicId,  // Long 타입으로 musicId를 받음
            @RequestParam(required = false) String username) {
        if (username == null || username.isEmpty()) {
            username = "anonymous";  // 비회원일 때 기본 사용자 ID
        }

        likeService.deleteLike(musicId);  // 이 메서드에서 Long 타입을 받아도 괜찮다면 그대로 둡니다
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}