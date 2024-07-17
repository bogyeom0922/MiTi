package com.MiTi.repository;

import com.MiTi.entity.Signin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SigninRepository extends JpaRepository<Signin, Integer> {
}
