package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final AlbumRepository albumRepository;

    public LikeService(LikeRepository likeRepository, AlbumRepository albumRepository) {
        this.likeRepository = likeRepository;
        this.albumRepository = albumRepository;
    }

    // 앨범에 대한 좋아요 추가 (빌더 패턴 사용)
    @Transactional
    public boolean addLike(String userId, String albumId) {
        if (likeRepository.existsByUserIdAndAlbumId(userId, albumId)) {
            return false; // 이미 좋아요가 존재함
        }

        Album album = albumRepository.findById(Long.parseLong(albumId))
                .orElseThrow(() -> new IllegalArgumentException("해당 앨범을 찾을 수 없습니다: " + albumId));

        Like like = Like.builder()
                .userId(userId)
                .albumId(albumId)
                .album(album)
                .build();
        likeRepository.save(like);
        return true;
    }

    // 좋아요 삭제
    @Transactional
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }

    // 앨범에 대한 좋아요 토글
    @Transactional
    public boolean toggleAlbumLike(String userId, String albumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, albumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            return addLike(userId, albumId);
        }
    }

    // 수록곡에 대한 좋아요 토글
    @Transactional
    public boolean toggleTrackLike(String userId, String trackId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, trackId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            Like newLike = Like.builder()
                    .userId(userId)
                    .albumId(trackId)
                    .build();
            likeRepository.save(newLike);
            return true;
        }
    }

    // 앨범 좋아요 여부 확인
    @Transactional
    public boolean isAlbumLikedByUser(String userId, String albumId) {
        return likeRepository.existsByUserIdAndAlbumId(userId, albumId);
    }

    // 트랙 좋아요 여부 확인
    @Transactional
    public boolean isTrackLikedByUser(String userId, String trackId) {
        return likeRepository.existsByUserIdAndAlbumId(userId, trackId);
    }

    // 사용자 ID로 좋아요 리스트 조회
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
                    .music_name(like.getAlbum().getMusicName())
                    .music_artist_name(like.getAlbum().getMusicArtistName())
                    .music_duration_ms(like.getAlbum().getMusic_duration_ms())
                    .build();
            likeDtoList.add(likeDto);
        }
        return likeDtoList;
    }
}
