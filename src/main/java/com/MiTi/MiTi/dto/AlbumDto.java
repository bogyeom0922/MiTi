package com.MiTi.MiTi.dto;

// AlbumDTO.java

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AlbumDto {

    private String musicName; // 카멜 케이스로 수정
    private String albumImage; // 카멜 케이스로 수정
    private String musicArtistName; // 카멜 케이스로 수정


    public AlbumDto(String musicName, String albumImage, String musicArtistName) {
        this.musicName = musicName;
        this.albumImage = albumImage;
        this.musicArtistName = musicArtistName;
    }

    // getters and setters
}
