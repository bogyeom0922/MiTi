package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Record;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecordDto {

    private String userId;
    private Long albumId;
    private String music_name;
    private String album_image;
    private String music_artist_name;
    private int music_duration_ms;  // 이미 int 타입으로 선언

    public Record toEntity() {
        return Record.builder()
                .userId(userId)
                .albumId(albumId)
                .build();
    }

    @Builder

    public RecordDto(String userId, Long albumId, String music_name, String album_image, String music_artist_name, int music_duration_ms) {

        this.userId = userId;
        this.albumId = albumId;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.music_duration_ms = music_duration_ms;  // int 타입으로 처리
    }

    public String getFormattedDuration() {
        // music_duration_ms는 이미 int 타입이므로 null 체크 불필요
        long minutes = music_duration_ms / 60000;
        long seconds = (music_duration_ms % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

}
