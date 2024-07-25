package com.MiTi.MiTi.repository;


import com.MiTi.MiTi.entity.Like;
import com.MiTi.MiTi.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

}
