package com.MiTi.MiTi.repository;


import com.MiTi.MiTi.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUserId(String userID);

    void deleteById(Long id);

}
