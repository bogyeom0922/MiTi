package com.MiTi.MiTi.dto;


import com.MiTi.MiTi.entity.Like;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LikeDto {

    private Long id;
    private String providerId;
    private Long albumId;
    private String music_name;
    private String album_image;
    private String music_artist_name;
    private Integer music_duration_ms;
    private String music_uri;

    public Like toEntity() {
        return Like.builder()
                .id(id)
                .providerId(providerId)
                .albumId(albumId)
                .build();
    }

    @Builder
    public LikeDto(Long id, String providerId, Long albumId, String music_name, String album_image, String music_artist_name, Integer music_duration_ms, String music_uri) {
        this.id=id;
        this.providerId = providerId;
        this.albumId = albumId;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.music_duration_ms = music_duration_ms;
        this.music_uri = music_uri != null ? music_uri.replace("\r", "") : null;
    }

    public String getFormattedDuration() {
        if (music_duration_ms == null) {
            return "00:00"; // 기본값 반환 (혹은 다른 처리)
        }

        Integer minutes = music_duration_ms / 60000;
        Integer seconds = (music_duration_ms % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

}
