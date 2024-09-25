package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Playlist;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlaylistDto {

    private Long id;
    private String userId;
    private Long albumId;
    private String userPlaylistName;
    private String userPlaylistImage;
    private String music_name;
    private String album_image;
    private String music_artist_name;

    // 추가된 필드
    private int totalSongs;
    private int totalDuration;

    @Builder
    public PlaylistDto(Long id, String userId, Long albumId, String userPlaylistName,
                       String userPlaylistImage, String music_name, String album_image, String music_artist_name,
                       int totalSongs, int totalDuration) {
        this.id = id;
        this.userId = userId;
        this.albumId = albumId;
        this.userPlaylistName = userPlaylistName;
        this.userPlaylistImage = userPlaylistImage;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.totalSongs = totalSongs;
        this.totalDuration = totalDuration;
    }

    // 필요한 setter 메서드
    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
}
