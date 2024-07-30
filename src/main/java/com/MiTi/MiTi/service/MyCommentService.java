package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.MyCommentDto;
import com.MiTi.MiTi.entity.MyComment;
import com.MiTi.MiTi.repository.MyCommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyCommentService {
    @Autowired
    private MyCommentRepository myCommentRepository;

    public List<MyComment> comments(String albumId) {
        return myCommentRepository.findByAlbumId(albumId);
    }

    @Transactional
    public List<MyCommentDto> getCommentListByUserId(String userId) {
        List<MyComment> myCommentList = myCommentRepository.findByUserId(userId);
        List<MyCommentDto> myCommentDtoList = new ArrayList<>();

        for (MyComment myComment : myCommentList) {
            MyCommentDto myCommentDto = MyCommentDto.builder()
                    .id(myComment.getId())
                    .userId(myComment.getUserId())
                    .albumId(myComment.getAlbumId())
                    .comment(myComment.getComment())
                    .album_image(myComment.getAlbum().getAlbum_image())
                    .music_name(myComment.getAlbum().getMusic_name())
                    .music_artist_name(myComment.getAlbum().getMusic_artist_name())
                    .build();
            myCommentDtoList.add(myCommentDto);
        }
        return myCommentDtoList;

    }


    @Transactional
    public void deleteComment(Long id) {
        myCommentRepository.deleteById(id);
    }

    // 댓글 수정 메서드 추가
    @Transactional
    public void updateMyComment(Long id, MyCommentDto myCommentDto) {
        MyComment myComment = myCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + id));

        myComment.setComment(myCommentDto.getComment());
        myCommentRepository.save(myComment);
    }
}
