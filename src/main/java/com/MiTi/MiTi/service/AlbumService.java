package com.MiTi.MiTi.service;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;


    public List<Album> findByDetail(String detail) {
        return albumRepository.findByDetail(detail);
    }

    public List<Album> findByMusicNameOrArtistName(String musicName, String artistName) {
        return albumRepository.findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(musicName, artistName); // 메서드 이름을 수정
    }
    public Optional<Album> findById(Long albumId) {
        return albumRepository.findById(albumId);
    }

}
