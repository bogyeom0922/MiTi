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
    private String musicName;
    private String albumImage;
    private String musicArtistName;
    private String musicDurationMs;

    public Like toEntity() {
        return Like.builder()
                .id(id)
                .userId(userId)
                .albumId(albumId)
                .build();
    }

    @Builder
    public LikeDto(Long id, String userId, String albumId, String musicName, String albumImage, String musicArtistName, String musicDurationMs) {
        this.id=id;
        this.userId = userId;
        this.albumId = albumId;
        this.musicName = musicName;
        this.albumImage = albumImage;
        this.musicArtistName = musicArtistName;
        this.musicDurationMs = musicDurationMs;
    }

    public String getFormattedDuration() {
        if (musicDurationMs == null) {
            return "00:00"; // 기본값 반환 (혹은 다른 처리)
        }

        long minutes = Integer.parseInt(musicDurationMs) / 60000;
        long seconds = (Integer.parseInt(musicDurationMs) % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

}
