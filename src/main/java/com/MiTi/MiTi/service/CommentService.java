package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.CommentDto;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> comments(String albumId) {
        return commentRepository.findByAlbumId(albumId);
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }
}
