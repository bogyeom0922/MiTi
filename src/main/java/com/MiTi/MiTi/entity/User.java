package com.MiTi.MiTi.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @EmbeddedId
    private UserId id;

    private String name;
    private String email;
    private String image;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    // RoleType 필드를 접근하기 위한 메서드
    public RoleType getRole() {
        return this.role;
    }

    // 필요시, RoleType 필드를 수정하는 메서드도 추가 가능합니다
    public void setRole(RoleType role) {
        this.role = role;
    }
}