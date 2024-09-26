package com.MiTi.MiTi.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "user_playlist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String providerId;

    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "user_playlist_name")
    private String userPlaylistName;

    @Column(name = "user_playlist_image")
    private String userPlaylistImage;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Album album;

}
