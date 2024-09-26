package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

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
    public boolean toggleAlbumLike(String providerId, String detail) {
        List<Album> albums = albumRepository.findByDetail(detail); // 앨범에 속한 곡들을 조회
        boolean isLiked = false;

        // 각 앨범(실제 곡들)에 대해 좋아요/좋아요 취소 처리
        for (Album album : albums) {
            Optional<Like> existingLike = likeRepository.findByProviderIdAndAlbumId(providerId, album.getId());

            if (existingLike.isPresent()) {
                // 이미 좋아요가 되어 있으면 좋아요 취소
                likeRepository.delete(existingLike.get());
            } else {
                // 좋아요 추가
                Like newLike = Like.builder()
                        .providerId(providerId)
                        .albumId(album.getId())  // albumId로 곡을 구분
                        .album(album)
                        .build();
                likeRepository.save(newLike);
                isLiked = true; // 하나라도 좋아요가 추가되면 true로 변경
            }
        }
        return isLiked; // 좋아요가 추가된 경우 true 반환
    }

    // 개별 곡에 대해 좋아요/취소 처리
    public boolean toggleTrackLike(String providerId, Long albumId) {
        Optional<Like> existingLike = likeRepository.findByProviderIdAndAlbumId(providerId, albumId);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 되어 있으면 좋아요 취소
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .providerId(providerId)
                    .albumId(albumId)  // albumId로 곡을 구분
                    .album(album)
                    .build();
            likeRepository.save(newLike);
            return true; // 좋아요 추가됨
        }
    }

    // 특정 앨범에 대해 좋아요 처리 (likeAlbum 메서드 추가)
    public void likeAlbum(Long albumId, Boolean isLiked, String providerId) {
        if (isLiked) {
            // 좋아요 추가
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 앨범을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .providerId(providerId)
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
        } else {
            // 좋아요 취소
            likeRepository.deleteByProviderIdAndAlbumId(providerId, albumId);
        }
    }

    // 새롭게 추가된 findByMusicNameOrArtistName 메서드
    public List<Album> findByMusicNameOrArtistName(String musicName, String artistName) {
        return albumRepository.findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(musicName, artistName);
    }


    //스트리밍
    //스트리밍에 필요함
    @Transactional
    public AlbumDto getAlbumDtoById(Long id) {
        return albumRepository.findById(id)
                .map(album -> AlbumDto.builder()
                        .id(album.getId())
                        .albumImage(album.getAlbum_image())
                        .musicName(album.getMusicName())
                        .musicArtistName(album.getMusicArtistName())
                        .music_duration_ms(album.getMusic_duration_ms())
                        .music_uri(album.getMusic_uri())
                        .build())
                .orElse(null);
    }

    @Transactional
    public Page<AlbumDto> getAlbumList(Pageable pageable) {
        Page<Album> albumPage = albumRepository.findAll((org.springframework.data.domain.Pageable) pageable);  // 페이지네이션 적용
        return albumPage.map(album -> AlbumDto.builder()
                .id(album.getId())
                .albumImage(album.getAlbum_image())
                .musicName(album.getMusicName())
                .musicArtistName(album.getMusicArtistName())
                .music_duration_ms(album.getMusic_duration_ms())
                .music_uri(album.getMusic_uri())
                .build());
    }


}