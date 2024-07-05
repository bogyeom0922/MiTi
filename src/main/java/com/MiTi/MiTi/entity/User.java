package com.MiTi.MiTi.entity;

import jakarta.persistence.*;

@Entity(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String user_id;

    @Column
    private String user_pw;

}
