package com.MiTi.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.net.ssl.SSLSession;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String  album_id;

    @Column
    private String user_id;

    @Column
    private String comment;

}
