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

    public List<MyComment> comments(Long albumId) {
        return myCommentRepository.findByAlbumId(albumId);
    }

    @Transactional
    public List<MyCommentDto> getMyCommentListByProviderId(String providerId) {
        List<MyComment> myCommentList = myCommentRepository.findByProviderId(providerId);
        List<MyCommentDto> myCommentDtoList = new ArrayList<>();

        for (MyComment myComment : myCommentList) {
            MyCommentDto myCommentDto = MyCommentDto.builder()
                    .id(myComment.getId())
                    .providerId(myComment.getProviderId())
                    .albumId(myComment.getAlbumId())
                    .comment(myComment.getComment())
                    .album_image(myComment.getAlbum().getAlbum_image())
                    .music_name(myComment.getAlbum().getMusicName())
                    .music_artist_name(myComment.getAlbum().getMusicArtistName())
                    .build();
            myCommentDtoList.add(myCommentDto);
        }
        return myCommentDtoList;

    }


    @Transactional
    public void deleteMyComment(Long id) {
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
