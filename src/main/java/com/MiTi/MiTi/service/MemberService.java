package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.entity.MemberEntity;
import com.MiTi.MiTi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    public boolean idCheck(String memberId) {
        logger.info("Checking if memberId exists: {}", memberId);
        return memberRepository.existsByMemberId(memberId);
    }

    public boolean emailCheck(String email) {
        logger.info("Checking if email exists: {}", email);
        return memberRepository.findByEmail(email).isPresent();
    }

    public String registerMember(MemberDTO memberDTO) {
        if (memberDTO == null) {
            logger.error("MemberDTO is null");
            return "error";
        }

        if (memberDTO.getMemberEmail() == null || memberDTO.getMemberEmail().isEmpty()) {
            logger.error("Member email is null or empty");
            return "error";
        }

        if (memberDTO.getMemberId() == null || memberDTO.getMemberId().isEmpty()) {
            logger.error("Member ID is null or empty");
            return "error";
        }

        if (idCheck(memberDTO.getMemberId())) {
            logger.info("Member ID already exists: {}", memberDTO.getMemberId());
            return "duplicate_id";
        }

        if (emailCheck(memberDTO.getMemberEmail())) {
            logger.info("Email already exists: {}", memberDTO.getMemberEmail());
            return "duplicate_email";
        }

        MemberEntity memberEntity = MemberEntity.fromDTO(memberDTO);
        memberRepository.save(memberEntity);
        logger.info("Member saved successfully: {}", memberDTO.getMemberId());

        return "ok";
    }

    public MemberDTO getMemberById(Long id) {
        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(id);
        return memberEntityOptional.map(MemberDTO::toMemberDTO).orElse(null);
    }

    public void updateMember(MemberDTO memberDTO) {
        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(memberDTO.getId());
        if (memberEntityOptional.isPresent()) {
            MemberEntity memberEntity = memberEntityOptional.get();
            memberEntity.setName(memberDTO.getMemberName());
            memberEntity.setPassword(memberDTO.getMemberPassword());
            memberRepository.save(memberEntity);
        }
    }
}
