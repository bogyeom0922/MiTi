/*

package com.MiTi.service;


// MypageService.java
import com.MiTi.dto.AlbumDto;
import com.MiTi.repository.AlbumRepository;
import com.MiTi.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private AlbumRepository albumRepository;

    public List<AlbumDto> getAlbumsByUser_id(Long user_id) {
        List<Record> records = RecordRepository.findByUser_id(user_id);
        return records.stream()
                .map(record -> albumRepository.findById(record.getAlbum_id()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(album -> new AlbumDto(album.getMusic_name(), album.getAlbum_image(), album.getMusic_artist_name()))
                .collect(Collectors.toList());
    }
}


*/
