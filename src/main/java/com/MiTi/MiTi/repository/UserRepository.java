package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.User;
import com.MiTi.MiTi.entity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {

    // UserId를 통해 사용자 정보 조회
    Optional<User> findById(UserId userId);

    // providerId로 User 조회하는 메서드 (Optional 사용)
    Optional<User> findByIdProviderId(String providerId);
}
