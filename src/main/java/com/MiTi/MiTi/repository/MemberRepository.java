package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByMemberId(String memberId);

    Optional<MemberEntity> findByEmail(String email);  // 이메일 필드에 맞게 수정

    boolean existsByMemberId(String memberId);

    // 추가적인 쿼리 메서드가 필요한 경우 여기에 추가할 수 있습니다.
}
