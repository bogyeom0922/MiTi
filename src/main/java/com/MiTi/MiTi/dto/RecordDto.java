package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Record;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecordDto {

    private String providerId;
    private Long albumId;
    private String music_name;
    private String album_image;
    private String music_artist_name;
    private String music_duration_ms;

    public Record toEntity() {
        return Record.builder()
                .providerId(providerId)
                .albumId(albumId)
                .build();
    }

    @Builder
    public RecordDto(String providerId, Long albumId, String music_name, String album_image, String music_artist_name, String music_duration_ms) {
        this.providerId = providerId;
        this.albumId = albumId;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.music_duration_ms = music_duration_ms;
    }

    public String getFormattedDuration() {
        if (music_duration_ms == null) {
            return "00:00"; // 기본값 반환 (혹은 다른 처리)
        }

        long minutes = Integer.parseInt(music_duration_ms) / 60000;
        long seconds = (Integer.parseInt(music_duration_ms) % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

}
