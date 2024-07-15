package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.CommentDto;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AlbumRepository albumRepository;

    //앨범 id로 댓글 조회 후 commentDto로 변환 후 반환
    public List<CommentDto> comments(Long albumId) {
        return commentRepository.findByAlbumId(albumId)
                .stream() //리스트에 저장된 요소 하나씩 반복 처리할 때
                .map(CommentDto::createCommentDto) //각 리뷰 객체 reviewDto 객체로 변환
                .collect(Collectors.toList());
    }

}
