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
    private String albumId;
    private String music_name;
    private String album_image;
    private String music_artist_name;
    private String music_duration_ms;
    private String music_uri;

    public Like toEntity() {
        return Like.builder()
                .id(id)
                .userId(userId)
                .albumId(albumId)
                .build();
    }

    @Builder
    public LikeDto(Long id, String userId, String albumId, String music_name, String album_image, String music_artist_name, String music_duration_ms, String music_uri) {
        this.id=id;
        this.userId = userId;
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

        long minutes = Integer.parseInt(music_duration_ms) / 60000;
        long seconds = (Integer.parseInt(music_duration_ms) % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

}
