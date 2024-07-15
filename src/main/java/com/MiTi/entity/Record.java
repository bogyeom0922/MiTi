package com.MiTi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "user_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@IdClass(RecordId.class)
public class Record {

    @Id
    private Integer user_id;

    @Id
    @Column(length = 10, nullable = false)
    private Integer album_id;

    @Builder
    public Record(Integer user_id, Integer album_id) {
        this.user_id = user_id;
        this.album_id = album_id;
    }
}
