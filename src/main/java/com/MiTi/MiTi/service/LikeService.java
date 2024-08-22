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

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final AlbumRepository albumRepository;

    public LikeService(LikeRepository likeRepository, AlbumRepository albumRepository) {
        this.likeRepository = likeRepository;
        this.albumRepository = albumRepository;
    }

    @Transactional
    public boolean addLike(String userId, Long trackId) {
        // Long 타입의 trackId를 String으로 변환
        String albumId = String.valueOf(trackId);

        // 이미 좋아요를 눌렀는지 확인
        if (likeRepository.existsByUserIdAndAlbumId(userId, albumId)) {
            return false; // 이미 좋아요가 존재함
        }

        // 앨범 정보 가져오기
        Album album = albumRepository.findById(trackId).orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

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

    @Transactional
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }
}
