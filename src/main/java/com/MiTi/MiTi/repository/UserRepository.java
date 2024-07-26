package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserId(String userId);
    boolean existsByUserMail(String userMail);
    Optional<UserEntity> findByUserMail(String userMail);
}
