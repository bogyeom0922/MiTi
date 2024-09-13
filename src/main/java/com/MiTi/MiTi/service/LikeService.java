package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private AlbumRepository albumRepository;

    // 특정 사용자(userId)와 앨범(albumId)에 대한 좋아요 여부 확인
    public boolean isAlbumLikedByUser(String userId, String albumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, albumId);
        return existingLike.isPresent();
    }

    // albumId를 기반으로 좋아요 상태를 토글하는 메서드 (userId 없이)
    public boolean toggleAlbumLike(String albumId) {
        Optional<Like> existingLike = likeRepository.findByAlbumId(albumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소
        } else {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
            return true; // 좋아요 추가
        }
    }

    // 특정 사용자(userId)와 앨범(albumId)에 대한 좋아요 상태를 토글하는 메서드
    public boolean toggleTrackLike(String userId, String albumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, albumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소
        } else {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .userId(userId)
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
            return true; // 좋아요 추가
        }
    }

    // 특정 트랙에 대해 좋아요 추가 메서드
    public boolean addLike(String username, String musicId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(username, musicId);
        if (existingLike.isPresent()) {
            return false; // 이미 좋아요가 되어 있음
        }

        Album album = albumRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + musicId));

        Like newLike = Like.builder()
                .userId(username)
                .albumId(musicId)
                .album(album)
                .build();
        likeRepository.save(newLike);
        return true; // 좋아요 추가됨
    }

    // 좋아요 취소 메서드
    public void deleteLike(Long musicId) {
        likeRepository.deleteByAlbumId(musicId.toString());
    }

    // 사용자가 좋아요한 앨범 목록 조회
    public List<LikeDto> getLikeListByUserId(String userId) {
        List<Like> likeList = likeRepository.findByUserId(userId);
        List<LikeDto> likeDtoList = new ArrayList<>();

        for (Like like : likeList) {
            LikeDto likeDto = LikeDto.builder()
                    .id(like.getId())
                    .userId(like.getUserId())
                    .albumId(like.getAlbumId())
                    .musicName(like.getAlbum().getMusicName())
                    .albumImage(like.getAlbum().getAlbum_image())  // 직접 정의한 getAlbum_image() 사용
                    .musicArtistName(like.getAlbum().getMusicArtistName())
                    .musicDurationMs(like.getAlbum().getMusic_duration_ms())  // 직접 정의한 getMusic_duration_ms() 사용
                    .build();
            likeDtoList.add(likeDto);
        }

        return likeDtoList;
    }
}
