package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Like;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LikeDto {

    private Long id;
    private String userId;
    private Long albumId;
    private String music_name;
    private String album_image;
    private String music_artist_name;
    private int music_duration_ms;  // 이미 int로 선언되어 있음
    private String music_uri;

    public Like toEntity() {
        return Like.builder()
                .id(id)
                .userId(userId)
                .albumId(albumId)
                .build();
    }

    @Builder
    public LikeDto(Long id, String userId, Long albumId, String music_name, String album_image, String music_artist_name, int music_duration_ms, String music_uri) {
        this.id = id;
        this.userId = userId;
        this.albumId = albumId;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.music_duration_ms = music_duration_ms;  // int로 처리
        this.music_uri = music_uri != null ? music_uri.replace("\r", "") : null;
    }

    public String getFormattedDuration() {
        // music_duration_ms는 이미 int 타입이므로 null 체크 필요 없음
        long minutes = music_duration_ms / 60000;
        long seconds = (music_duration_ms % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

}
