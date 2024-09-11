package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 정보 가져오기
    @GetMapping("/{provider}/{providerId}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable String provider, @PathVariable String providerId) {
        Optional<MemberDTO> memberDTO = memberService.getMemberById(provider, providerId);
        if (memberDTO.isPresent()) {
            return new ResponseEntity<>(memberDTO.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 회원 정보 저장하기
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO savedMember = memberService.saveMember(memberDTO);
        return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }
}
