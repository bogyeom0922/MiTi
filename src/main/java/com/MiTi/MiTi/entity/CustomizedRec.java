package com.MiTi.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "customized_rec")
@IdClass(CustomizedRecId.class)  // 복합 키 설정
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedRec {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Id
    @Column(name = "album_id", nullable = false)
    private Long albumId;

    // 다른 필드 및 로직 추가 가능
}
