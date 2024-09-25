package com.MiTi.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;
import javax.sound.midi.Track;
import java.util.List;

@Getter
@Setter
@Entity(name = "album")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "music_name")
    private String musicName;
    private String music_id;
    private String music_popularity;
    @Column(name = "album_image")
    private String album_image;
    @Column(name = "detail")
    private String detail;
    @Column(name = "music_artist_name")
    private String musicArtistName;
    private String music_artist_id;
    private String music_artist_popularity;
    private String music_genre;
    private String music_artist_genres;
    private String music_artist_followers;
    private String music_analysis_url;
    private String music_key;
    @Column(name = "music_duration_ms")
    private int music_duration_ms;
    private Double music_instrumentalness;  // 악기 특성 (0.0 ~ 1.0)
    private Double music_acousticness;      // 어쿠스틱 특성 (0.0 ~ 1.0)
    private Double music_danceability;      // 댄서블 특성 (0.0 ~ 1.0)
    private Double music_energy;            // 에너지 레벨 (0.0 ~ 1.0)
    private Double music_liveness;          // 라이브한 정도 (0.0 ~ 1.0)
    private Double music_loudness;          // 음량 (-60.0 ~ 0.0 dB)
    private Double music_mode;              // 장조/단조 (0 또는 1)
    private Double music_speechiness;       // 스피치 특성 (0.0 ~ 1.0)
    private Double music_tempo;             // 템포 (BPM)
    private Double music_time_signature;    // 박자 (보통 3, 4, 5)
    private Double music_valence;
    private String music_track_href;
    private String music_type;
    private String music_uri;

    @Column(name = "is_liked")
    private Boolean isLiked;

    // 기존의 getter 메서드를 수정
    public String getAlbum_image() {
        return album_image;
    }

    public int getMusic_duration_ms() {  // 리턴 타입을 int로 수정
        return music_duration_ms;
    }

    // setter 메서드도 필요하면 추가
    public void setMusic_duration_ms(int music_duration_ms) {
        this.music_duration_ms = music_duration_ms;
    }

}