package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.entity.Record;
import com.MiTi.MiTi.repository.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Transactional
    public List<LikeDto> getLikeListByUserId(String userId) {
        List<Like> likeList = likeRepository.findByUserId(userId);
        List<LikeDto> likeDtoList = new ArrayList<>();

        for (Like like : likeList) {
            LikeDto likeDto = LikeDto.builder()
                    .id(like.getId())
                    .userId(like.getUserId())
                    .albumId(like.getAlbumId())
                    .album_image(like.getAlbum().getAlbum_image())
                    .music_name(like.getAlbum().getMusic_name())
                    .music_artist_name(like.getAlbum().getMusic_artist_name())
                    .music_duration_ms(like.getAlbum().getMusic_duration_ms())
                    .build();
            likeDtoList.add(likeDto);
        }
        return likeDtoList;

    }


    @Transactional
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }
}



