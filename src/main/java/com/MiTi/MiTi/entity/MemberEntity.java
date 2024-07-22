package com.MiTi.MiTi.entity;

import com.MiTi.MiTi.dto.MemberDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", unique = true, nullable = false)
    private String memberId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "auth_code")
    private String authCode;

    @Column(name = "auth_code_expiration")
    private LocalDateTime authCodeExpiration;

    // DTO를 Entity로 변환하는 메서드
    public static MemberEntity toMemberEntity(MemberDTO dto) {
        MemberEntity entity = new MemberEntity();
        entity.setMemberId(dto.getMemberId());
        entity.setName(dto.getMemberName());
        entity.setEmail(dto.getMemberEmail());
        entity.setPassword(dto.getMemberPassword());
        return entity;
    }

    // 업데이트 시 사용되는 MemberDTO를 Entity로 변환하는 메서드
    public static MemberEntity toUpdateMemberEntity(MemberDTO dto) {
        MemberEntity entity = new MemberEntity();
        entity.setId(dto.getId());
        entity.setMemberId(dto.getMemberId());
        entity.setName(dto.getMemberName());
        entity.setEmail(dto.getMemberEmail());
        entity.setPassword(dto.getMemberPassword());
        return entity;
    }
}

