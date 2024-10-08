package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    private AlbumRepository albumRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    // 특정 사용자(userId)와 앨범(albumId)에 대한 좋아요 여부 확인
    public boolean isAlbumLikedByUser(String providerId, Long albumId) {
        Optional<Like> existingLike = likeRepository.findByProviderIdAndAlbumId(providerId, albumId);
        return existingLike.isPresent();
    }



    // 특정 사용자(userId)와 앨범(albumId)에 대한 좋아요 상태를 토글하는 메서드
    public boolean toggleTrackLike(String providerId, Long albumId) {
        Optional<Like> existingLike = likeRepository.findByProviderIdAndAlbumId(providerId, albumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소
        } else {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .providerId(providerId)
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
            return true; // 좋아요 추가
        }
    }

    // 특정 트랙에 대해 좋아요 추가 메서드
    public boolean addLike(String providerId, Long musicId) {
        Optional<Like> existingLike = likeRepository.findByProviderIdAndAlbumId(providerId, musicId);
        if (existingLike.isPresent()) {
            return false; // 이미 좋아요가 되어 있음
        }

        Album album = albumRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + musicId));

        Like newLike = Like.builder()
                .providerId(providerId)
                .albumId(musicId)
                .album(album)
                .build();
        likeRepository.save(newLike);
        return true; // 좋아요 추가됨
    }

    //스트리밍에 필요함
    @Transactional
    public LikeDto getLikeDtoById(Long id) {
        return likeRepository.findById(id)
                .map(like -> LikeDto.builder()
                        .id(like.getId())
                        .providerId(like.getProviderId())
                        .albumId(like.getAlbumId())
                        .album_image(like.getAlbum().getAlbum_image())
                        .music_name(like.getAlbum().getMusicName())
                        .music_artist_name(like.getAlbum().getMusicArtistName())
                        .music_duration_ms(like.getAlbum().getMusic_duration_ms())
                        .music_uri(like.getAlbum().getMusic_uri())
                        .build())
                .orElse(null);
    }


    @Transactional
    public List<LikeDto> getLikeListByUserId(String providerId) {
        List<Like> likeList = likeRepository.findByProviderId(providerId);
        List<LikeDto> likeDtoList = new ArrayList<>();

        for (Like like : likeList) {
            LikeDto likeDto = LikeDto.builder()
                    .id(like.getId())
                    .providerId(like.getProviderId())
                    .albumId(like.getAlbumId())
                    .album_image(like.getAlbum().getAlbum_image())
                    .music_name(like.getAlbum().getMusicName())
                    .music_artist_name(like.getAlbum().getMusicArtistName())
                    .music_duration_ms(like.getAlbum().getMusic_duration_ms())
                    .music_uri(like.getAlbum().getMusic_uri())
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



