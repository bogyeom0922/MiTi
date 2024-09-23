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
    private String albumId;
    private String userPlaylistName;
    private String userPlaylistImage;
    private String music_name;
    private String album_image;
    private String music_artist_name;

        public Playlist toEntity() {
        return Playlist.builder()
                .id(id)
                .userId(userId)
                .albumId(albumId)
                .userPlaylistName(userPlaylistName)
                .userPlaylistImage(userPlaylistImage)
                .build();
    }

    @Builder
    public PlaylistDto(Long id, String userId, String albumId, String userPlaylistName,
                       String userPlaylistImage, String music_name, String album_image, String music_artist_name) {
        this.id=id;
        this.userId = userId;
        this.albumId = albumId;
        this.userPlaylistName=userPlaylistName;
        this.userPlaylistImage=userPlaylistImage;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;

    }

}
