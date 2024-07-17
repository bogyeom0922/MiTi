package com.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_genre")
public class Signin {

    @Id
    private Integer user_id;

    @Column(length = 20, nullable = false)
    private String genre;

    @Builder
    public Signin(Integer user_id, String genre) {
        this.user_id = user_id;
        this.genre = genre;
    }
}
