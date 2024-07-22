package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.MemberEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MemberDTO {
    private Long id;
    private String memberId;         // 아이디
    private String memberName;       // 이름
    private String memberEmail;      // 이메일
    private String memberPassword;   // 비밀번호

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setMemberId(memberEntity.getMemberId());
        memberDTO.setMemberName(memberEntity.getName());
        memberDTO.setMemberEmail(memberEntity.getEmail());
        memberDTO.setMemberPassword(memberEntity.getPassword());
        return memberDTO;
    }
}
