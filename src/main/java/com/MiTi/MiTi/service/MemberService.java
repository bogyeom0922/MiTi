package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.entity.MemberEntity;
import com.MiTi.MiTi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 이메일 중복 체크
    public String emailCheck(String memberEmail) {
        Optional<MemberEntity> existingMember = memberRepository.findByMemberEmail(memberEmail);
        return existingMember.isPresent() ? "duplicate" : "ok";
    }

    // 아이디 중복 체크
    public boolean idCheck(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    // 회원 등록
    public String registerMember(MemberDTO memberDTO) {
        // 아이디 중복 체크
        if (idCheck(memberDTO.getMemberId())) {
            return "duplicate"; // 중복된 아이디
        }

        // 이메일 중복 체크
        if ("duplicate".equals(emailCheck(memberDTO.getMemberEmail()))) {
            return "duplicate"; // 중복된 이메일
        }

        // DTO를 Entity로 변환하여 저장
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);

        return "ok"; // 회원가입 성공
    }
}
