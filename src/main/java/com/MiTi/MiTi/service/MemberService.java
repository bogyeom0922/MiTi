package com.MiTi.MiTi.service;

import com.MiTi.MiTi.OAuth2.OAuth2Provider;
import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.entity.Member;
import com.MiTi.MiTi.entity.MemberId;
import com.MiTi.MiTi.entity.RoleType;
import com.MiTi.MiTi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 정보 가져오기
    public Optional<MemberDTO> getMemberById(String provider, String providerId) {
        MemberId memberId = MemberId.builder()
                .provider(OAuth2Provider.valueOf(provider))  // OAuth2Provider 열거형의 적절한 값을 사용
                .providerId(providerId)
                .build();

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            MemberDTO memberDTO = MemberDTO.builder()
                    .provider(member.getId().getProvider().name())
                    .providerId(member.getId().getProviderId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .image(member.getImage())
                    .role(member.getRole().name())
                    .build();
            return Optional.of(memberDTO);
        }
        return Optional.empty();
    }

    // 회원 정보 저장하기
    public MemberDTO saveMember(MemberDTO memberDTO) {
        MemberId memberId = MemberId.builder()
                .provider(OAuth2Provider.valueOf(memberDTO.getProvider())) // OAuth2Provider 열거형의 적절한 값을 사용
                .providerId(memberDTO.getProviderId())
                .build();

        Member member = Member.builder()
                .id(memberId)
                .name(memberDTO.getName())
                .email(memberDTO.getEmail())
                .image(memberDTO.getImage())
                .role(RoleType.valueOf(memberDTO.getRole()))
                .build();

        Member savedMember = memberRepository.save(member);
        return MemberDTO.builder()
                .provider(savedMember.getId().getProvider().name())
                .providerId(savedMember.getId().getProviderId())
                .name(savedMember.getName())
                .email(savedMember.getEmail())
                .image(savedMember.getImage())
                .role(savedMember.getRole().name())
                .build();
    }
}
