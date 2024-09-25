package com.MiTi.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "user_id")
    private String userId;

    @Column
    private String comment;

    public Comment(Long albumId, String userId, String comment) {
        this.albumId = albumId;
        this.userId = userId;
        this.comment = comment;
    }

}