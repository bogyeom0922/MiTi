package com.MiTi.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
