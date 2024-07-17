

package com.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;



@Entity
@Getter
@Setter
@Table(name = "user_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@IdClass(LikeId.class)
@Builder
@AllArgsConstructor // 모든 필드를 포함하는 생성자 추가
public class Like {

    @Id
    private Integer user_id;

    @Id
    @Column(length = 10, nullable = false)
    private Integer album_id;

    @Column(name = "is_like")
    private Boolean isLike;

}


