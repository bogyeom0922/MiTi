package com.MiTi.MiTi.service;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private LikeRepository likeRepository;

    // 상세 정보(detail)를 통해 앨범 목록 검색
    public List<Album> findByDetail(String detail) {
        return albumRepository.findByDetail(detail);
    }

    // 앨범에 속한 모든 곡을 좋아요/좋아요 취소 처리
    public boolean toggleAlbumLike(String userId, String detail) {
        List<Album> albums = albumRepository.findByDetail(detail); // 앨범에 속한 곡들을 조회
        boolean isLiked = false;

        // 각 앨범(실제 곡들)에 대해 좋아요/좋아요 취소 처리
        for (Album album : albums) {
            Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, album.getId().toString());

            if (existingLike.isPresent()) {
                // 이미 좋아요가 되어 있으면 좋아요 취소
                likeRepository.delete(existingLike.get());
            } else {
                // 좋아요 추가
                Like newLike = Like.builder()
                        .userId(userId)
                        .albumId(album.getId().toString())  // albumId로 곡을 구분
                        .album(album)
                        .build();
                likeRepository.save(newLike);
                isLiked = true; // 하나라도 좋아요가 추가되면 true로 변경
            }
        }
        return isLiked; // 좋아요가 추가된 경우 true 반환
    }

    // 개별 곡에 대해 좋아요/취소 처리
    public boolean toggleTrackLike(String userId, String albumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, albumId);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 되어 있으면 좋아요 취소
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .userId(userId)
                    .albumId(albumId)  // albumId로 곡을 구분
                    .album(album)
                    .build();
            likeRepository.save(newLike);
            return true; // 좋아요 추가됨
        }
    }

    // 특정 앨범에 대해 좋아요 처리 (likeAlbum 메서드 추가)
    public void likeAlbum(String albumId, Boolean isLiked, String userId) {
        if (isLiked) {
            // 좋아요 추가
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 앨범을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .userId(userId)
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
        } else {
            // 좋아요 취소
            likeRepository.deleteByUserIdAndAlbumId(userId, albumId);
        }
    }

    // 새롭게 추가된 findByMusicNameOrArtistName 메서드
    public List<Album> findByMusicNameOrArtistName(String musicName, String artistName) {
        return albumRepository.findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(musicName, artistName);
    }
}
