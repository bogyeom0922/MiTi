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
    public List<RecordDto> getRecordListByProviderId(String providerId) {
        List<Record> recordList = recordRepository.findByProviderId(providerId);
        List<RecordDto> recordDtoList = new ArrayList<>();

        for (Record record : recordList) {
            RecordDto recordDto = RecordDto.builder()
                    .providerId(record.getProviderId())
                    .albumId(record.getAlbumId())
                    .album_image(record.getAlbum().getAlbum_image())
                    .music_name(record.getAlbum().getMusicName())
                    .music_artist_name(record.getAlbum().getMusicArtistName())
                    .music_duration_ms(record.getAlbum().getMusic_duration_ms())  // int 타입으로 처리
                    .build();
            recordDtoList.add(recordDto);
        }
        return recordDtoList;
    }



    @Transactional
    public void recordMusic(RecordDto recordDto) {  //스트리밍 시 음악 기록하기
        System.out.println("Recording music for albumId: " + recordDto.getAlbumId());
        Record record = Record.builder()
                .providerId(recordDto.getProviderId())
                .albumId(recordDto.getAlbumId())
                .build();
        recordRepository.save(record);
    }
}