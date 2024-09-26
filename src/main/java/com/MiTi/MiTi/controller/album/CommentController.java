package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.dto.CommentDto;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/submitComment")
    public ResponseEntity<String> submitComment(@RequestBody CommentDto commentDto) {
        Comment comment = new Comment(
                commentDto.getAlbumId(),
                commentDto.getProviderId(),
                commentDto.getComment()
        );
        commentService.saveComment(comment);
        return ResponseEntity.ok("Comment saved successfully");
    }

}