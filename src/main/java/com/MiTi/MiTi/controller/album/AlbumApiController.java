package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/album")
public class AlbumApiController {

    @Autowired
    private AlbumService albumService;

    @PostMapping("/{albumId}/like")
    public ResponseEntity<String> likeAlbum(@PathVariable Long albumId, @RequestParam Boolean isLiked, @RequestParam String providerId) {
        // userId를 추가로 전달
        albumService.likeAlbum(albumId, isLiked, providerId);
        return ResponseEntity.ok(isLiked ? "좋아요" : "좋아요 취소");
    }
}