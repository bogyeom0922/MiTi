package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserId(String userId);
    boolean existsByUserMail(String userMail);
    Optional<UserEntity> findByUserMail(String userMail); // 변경 사항
}
