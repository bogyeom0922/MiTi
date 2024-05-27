package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Album;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class AlbumDto {
    private long id;
    private String music_name;
    private String music_id;
    private String music_popularity;
    private String album_image;
    private String album_detail;
    private String music_artist_name;
    private String music_artist_id;
    private String music_artist_popularity;
    private String music_genre;
    private String music_artist_genres;
    private String music_artist_followers;
    private String music_analysis_url;
    private String music_key;
    private String music_duration_ms;
    private String music_instrumentalness;
    private String music_acousticness;
    private String music_danceability;
    private String music_energy;
    private String music_liveness;
    private String music_loudness;
    private String music_mode;
    private String music_speechiness;
    private String music_tempo;
    private String music_time_signature;
    private String music_valence;
    private String music_track_href;
    private String music_type;
    private String music_uri;

    public Album toEntity() {
        return Album.builder()
                .id(id)
                .music_name(music_name)
                .music_id(music_id)
                .music_popularity(music_popularity)
                .album_image(album_image)
                .album_detail(album_detail)
                .music_artist_name(music_artist_name)
                .music_artist_id(music_artist_id)
                .music_artist_popularity(music_artist_popularity)
                .music_genre(music_genre)
                .music_artist_genres(music_artist_genres)
                .music_artist_followers(music_artist_followers)
                .music_analysis_url(music_analysis_url)
                .music_key(music_key)
                .music_duration_ms(music_duration_ms)
                .music_instrumentalness(music_instrumentalness)
                .music_acousticness(music_acousticness)
                .music_danceability(music_danceability)
                .music_energy(music_energy)
                .music_liveness(music_liveness)
                .music_loudness(music_loudness)
                .music_mode(music_mode)
                .music_speechiness(music_speechiness)
                .music_tempo(music_tempo)
                .music_time_signature(music_time_signature)
                .music_valence(music_valence)
                .music_track_href(music_track_href)
                .music_type(music_type)
                .music_uri(music_uri)
                .build();
    }
}
