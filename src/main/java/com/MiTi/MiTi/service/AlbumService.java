package com.MiTi.MiTi.service;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Track;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TrackRepository trackRepository;

    // 앨범 ID로 앨범 가져오기
    public Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("해당 앨범을 찾을 수 없습니다: " + albumId));
    }

    // 상세 정보로 앨범 검색
    public List<Album> findByDetail(String detail) {
        return albumRepository.findByDetail(detail);
    }

    // 음악 이름 또는 아티스트 이름으로 앨범 검색
    public List<Album> findByMusicNameOrArtistName(String musicName, String artistName) {
        return albumRepository.findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(musicName, artistName);
    }

    // 앨범 ID로 찾기
    public Optional<Album> findById(Long albumId) {
        return albumRepository.findById(albumId);
    }

    // 앨범 및 모든 수록곡 좋아요 상태 업데이트
    public void likeAlbum(Long albumId, Boolean isLiked) {
        // 앨범 찾기
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));

        // 앨범의 좋아요 상태 설정
        album.setIsLiked(isLiked);
        albumRepository.save(album);

        // 해당 앨범의 모든 수록곡의 좋아요 상태 업데이트
        for (Track track : album.getTracks()) {
            track.setIsLiked(isLiked);
            trackRepository.save(track);
        }
    }
}
