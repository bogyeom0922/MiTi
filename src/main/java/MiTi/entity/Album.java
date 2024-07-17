package MiTi.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="album")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자 추가
 // 모든 필드를 포함한 생성자 추가
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String music_name;

    @Column(length = 300, nullable = false)
    private String album_image;

    @Column(length = 50, nullable = false)
    private String music_artist_name;


    @Column(length = 50, nullable = false)
    private Integer music_duration_ms;

    @Builder
    public Album(Integer album_id, String music_name, String album_image, String music_artist_name, Integer music_duration_ms ) {
        this.id = id;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
        this.music_duration_ms = music_duration_ms;
    }

}
