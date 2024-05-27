package com.MiTi.MiTi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Entity(name = "album")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
