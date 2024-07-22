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

    private String music_name;
    private String album_image;
    private String music_artist_name;

    public AlbumDto(String music_name, String album_image, String music_artist_name) {
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
    }

    // getters and setters
}

