package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.OAuth2.OAuth2Provider;
import com.MiTi.MiTi.entity.User;
import com.MiTi.MiTi.entity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {

}
