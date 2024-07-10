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
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String memberId;

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberId(memberEntity.getMemberId());
        return memberDTO;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
                "id=" + id +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberPassword='" + memberPassword + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberId='" + memberId + '\'' +
                '}';
    }
}
