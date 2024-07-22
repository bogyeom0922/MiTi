package com.MiTi.MiTi.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "user_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@IdClass(LikeId.class)
public class Like {

    @Id
    private Integer user_id;

    @Id
    private Integer album_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Album album;
}
