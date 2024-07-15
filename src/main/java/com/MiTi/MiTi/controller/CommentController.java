package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.CommentDto;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void submitComment(@RequestBody CommentDto commentDto) {
        // CommentDto를 Comment 엔티티로 변환하여 서비스로 전달
        Comment comment = new Comment(
                commentDto.getAlbumId(),
                commentDto.getUserId(),
                commentDto.getComment()
        );
        commentService.saveComment(comment);
    }
}
