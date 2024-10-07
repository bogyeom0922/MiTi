package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.dto.CommentDto;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/submitComment")
    public ResponseEntity<Map<String, String>> submitComment(@RequestBody CommentDto commentDto) {
        Comment comment = new Comment(
                commentDto.getAlbumId(),
                commentDto.getProviderId(),
                commentDto.getComment()
        );
        commentService.saveComment(comment);

        // JSON 응답으로 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment saved successfully");
        return ResponseEntity.ok(response);
    }

}