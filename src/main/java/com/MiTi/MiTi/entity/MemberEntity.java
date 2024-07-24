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
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;  // 여전히 Long 타입을 사용, JPA는 Long 타입을 권장

    @Column(name = "member_id", unique = true, nullable = false, length = 15)
    private String memberId;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
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

    // DTO를 Entity로 변환하는 메서드 (null 체크 포함)
    public static MemberEntity fromDTO(MemberDTO dto) {
        if (dto == null) {
            return null;
        }
        MemberEntity entity = new MemberEntity();
        if (dto.getId() != null) {
            entity.setId(dto.getId()); // ID는 주어진 경우만 설정
        }
        entity.setMemberId(dto.getMemberId());
        entity.setName(dto.getMemberName());
        entity.setEmail(dto.getMemberEmail()); // 이 부분에서 null이 아닌지 확인
        entity.setPassword(dto.getMemberPassword());
        return entity;
    }
}