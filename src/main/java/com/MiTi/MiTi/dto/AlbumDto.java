package com.MiTi.MiTi.dto;

// AlbumDTO.java

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AlbumDto {

    private Long id;
    private String musicName; // 카멜 케이스로 수정
    private String music_uri;
    private String albumImage; // 카멜 케이스로 수정
    private String musicArtistName; // 카멜 케이스로 수정
    private Integer music_duration_ms;
    private String detail;

    @Builder
    public AlbumDto(Long id, String musicName, String albumImage, String musicArtistName, Integer music_duration_ms, String music_uri, String detail) {
        this.id = id;
        this.musicName = musicName;
        this.albumImage = albumImage;
        this.musicArtistName = musicArtistName;
        this.music_duration_ms = music_duration_ms;
        this.music_uri = music_uri;
        this.detail = detail;
    }

    // getters and setters
}