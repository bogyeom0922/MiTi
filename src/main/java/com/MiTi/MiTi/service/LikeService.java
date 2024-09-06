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

    // Long 타입의 albumId를 처리하는 오버로딩 메서드 추가
    @Transactional
    public boolean addLike(String userId, Long albumId) {
        return addLike(userId, String.valueOf(albumId));
    }

    @Transactional
    public boolean addLike(String userId, String albumId) {
        if (likeRepository.existsByUserIdAndAlbumId(userId, albumId)) {
            return false; // 이미 좋아요가 존재함
        }
//앨범 정보 가져오기
        Album album = albumRepository.findById(Long.parseLong(albumId))
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));
// 새로운 좋아요 엔티티 생성 및 저장
        Like like = Like.builder()
                .userId(userId)
                .albumId(albumId)
                .album(album)
                .build();
        likeRepository.save(like);
        return true;
    }

    @Transactional
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }

    @Transactional
    public boolean toggleLike(String userId, String albumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, albumId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소
        } else {
            return addLike(userId, albumId); // 좋아요 추가
        }
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
                    .music_name(like.getAlbum().getMusicName())
                    .music_artist_name(like.getAlbum().getMusicArtistName())
                    .music_duration_ms(like.getAlbum().getMusic_duration_ms())
                    .build();
            likeDtoList.add(likeDto);
        }
        return likeDtoList;
    }
}
