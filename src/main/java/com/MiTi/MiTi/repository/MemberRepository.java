package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByMemberEmail(String memberEmail);

    // 아이디로 회원 찾기 메서드 추가 가능
}
