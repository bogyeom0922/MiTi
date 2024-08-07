package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.entity.Record;
import com.MiTi.MiTi.repository.RecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecordService {
    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Transactional
    public List<RecordDto> getRecordListByUserId(String userId) {
        List<Record> recordList = recordRepository.findByUserId(userId);
        List<RecordDto> recordDtoList = new ArrayList<>();

        for (Record record : recordList) {
            RecordDto recordDto = RecordDto.builder()
                    .userId(record.getUserId())
                    .albumId(record.getAlbumId())
                    .album_image(record.getAlbum().getAlbum_image())
                    .music_name(record.getAlbum().getMusic_name())
                    .music_artist_name(record.getAlbum().getMusic_artist_name())
                    .music_duration_ms(record.getAlbum().getMusic_duration_ms())
                    .build();
            recordDtoList.add(recordDto);
        }
        return recordDtoList;
    }
}