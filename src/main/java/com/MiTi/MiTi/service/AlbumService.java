package com.MiTi.MiTi.service;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<String> getAllAlbumDetails() {
        return albumRepository.findAll().stream()
                .map(Album::getAlbumDetail)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Album> getAlbumsByDetail(String albumDetail) {
        return albumRepository.findByAlbumDetail(albumDetail);
    }
}
