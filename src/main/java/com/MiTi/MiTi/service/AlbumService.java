package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.dto.LikeDto;
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
    public boolean toggleAlbumLike(String userId, String detail) {
        List<Album> albums = albumRepository.findByDetail(detail); // 앨범에 속한 곡들을 조회
        boolean isLiked = false;

        for (Album album : albums) {
            Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, album.getId());

            if (existingLike.isPresent()) {
                // 좋아요가 되어 있으면 취소
                likeRepository.delete(existingLike.get());
            } else {
                // 좋아요 추가
                Like newLike = Like.builder()
                        .userId(userId)
                        .albumId(album.getId())  // albumId로 곡을 구분
                        .album(album)
                        .build();
                likeRepository.save(newLike);
                isLiked = true;
            }
        }
        return isLiked;
    }

    // 개별 곡에 대해 좋아요/취소 처리
    public boolean toggleTrackLike(String userId, Long albumId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndAlbumId(userId, albumId);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            // 좋아요 추가
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 곡을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .userId(userId)
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
            return true;
        }
    }

    // 특정 앨범 좋아요 처리
    public void likeAlbum(Long albumId, Boolean isLiked, String userId) {
        if (isLiked) {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("해당 앨범을 찾을 수 없습니다: " + albumId));

            Like newLike = Like.builder()
                    .userId(userId)
                    .albumId(albumId)
                    .album(album)
                    .build();
            likeRepository.save(newLike);
        } else {
            likeRepository.deleteByUserIdAndAlbumId(userId, albumId);
        }
    }

    // 음악 이름 또는 아티스트 이름으로 검색
    public List<Album> findByMusicNameOrArtistName(String musicName, String artistName) {
        return albumRepository.findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(musicName, artistName);
    }

    // 스트리밍
    @Transactional
    public AlbumDto getAlbumDtoById(Long id) {
        return albumRepository.findById(id)
                .map(album -> AlbumDto.builder()
                        .id(album.getId())
                        .albumImage(album.getAlbum_image())
                        .musicName(album.getMusicName())
                        .musicArtistName(album.getMusicArtistName())
                        .music_duration_ms(album.getMusic_duration_ms())  // int 처리
                        .music_uri(album.getMusic_uri())
                        .build())
                .orElse(null);
    }

    @Transactional
    public Page<AlbumDto> getAlbumList(Pageable pageable) {
        Page<Album> albumPage = albumRepository.findAll(pageable);
        return albumPage.map(album -> AlbumDto.builder()
                .id(album.getId())
                .albumImage(album.getAlbum_image())
                .musicName(album.getMusicName())
                .musicArtistName(album.getMusicArtistName())
                .music_duration_ms(album.getMusic_duration_ms())  // int 처리
                .music_uri(album.getMusic_uri())
                .build());
    }
}
