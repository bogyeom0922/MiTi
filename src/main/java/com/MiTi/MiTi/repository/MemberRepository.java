package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Member;
import com.MiTi.MiTi.entity.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, MemberId> {

    // Optional<Member> findByEmail(String email);  // 예시: 이메일을 기반으로 사용자 조회

    // Optional<Member> findByIdAndProvider(String id, OAuth2Provider provider);  // 예시: id와 provider를 기반으로 사용자 조회
}
