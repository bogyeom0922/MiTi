package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Playlist;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlaylistDto {

    private Long id;
    private String providerId;
    private Long albumId;
    private String userPlaylistName;
    private String userPlaylistImage;
    private String music_name;
    private String album_image;
    private String music_artist_name;
    private String detail;
    private String music_uri;
    private Integer music_duration_ms;

    // 필요한 setter 메서드
    // 추가된 필드
    @Setter
    private int totalSongs;
    @Setter
    private int totalDuration;



    public Playlist toEntity() {
        return Playlist.builder()
                .id(id)
                .providerId(providerId)
                .albumId(albumId)
                .userPlaylistName(userPlaylistName)
                .userPlaylistImage(userPlaylistImage)
                .build();
    }

    @Builder
    public PlaylistDto(Long id, String providerId, Long albumId, String userPlaylistName,
                       String userPlaylistImage, String music_name, String album_image, String music_artist_name, String detail, String music_uri, Integer music_duration_ms) {
        this.id=id;
        this.providerId = providerId;
        this.albumId = albumId;
        this.userPlaylistName=userPlaylistName;
        this.userPlaylistImage=userPlaylistImage;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.music_uri= music_uri;
        this.detail = detail;
    }

}
