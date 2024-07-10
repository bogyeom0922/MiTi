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

    public String registerMember(MemberDTO memberDTO) {
        // 이메일 중복 체크
        Optional<MemberEntity> existingMember = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (existingMember.isPresent()) {
            return "duplicate"; // 이미 존재하는 이메일이므로 실패 메시지 반환
        }

        // 아이디 중복 체크도 추가 가능

        // 회원 등록
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);
        return "ok"; // 회원가입 성공 메시지 반환
    }

    public MemberDTO login(MemberDTO memberDTO) {
        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (byMemberEmail.isPresent()) {
            MemberEntity memberEntity = byMemberEmail.get();
            if (memberEntity.getMemberPassword().equals(memberDTO.getMemberPassword())) {
                return MemberDTO.toMemberDTO(memberEntity);
            }
        }
        return null; // 로그인 실패 시 null 반환
    }

    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        return memberEntityList.stream()
                .map(MemberDTO::toMemberDTO)
                .collect(Collectors.toList());
    }

    public MemberDTO findById(Long id) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
        return optionalMemberEntity.map(MemberDTO::toMemberDTO).orElse(null);
    }

    public MemberDTO updateForm(String myEmail) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByMemberEmail(myEmail);
        return optionalMemberEntity.map(MemberDTO::toMemberDTO).orElse(null);
    }

    public void update(MemberDTO memberDTO) {
        memberRepository.save(MemberEntity.toUpdateMemberEntity(memberDTO));
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    public String emailCheck(String memberEmail) {
        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberEmail);
        return byMemberEmail.isPresent() ? "duplicate" : "ok";
    }
}
