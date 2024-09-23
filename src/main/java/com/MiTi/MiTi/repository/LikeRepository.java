package com.MiTi.MiTi.repository;


import com.MiTi.MiTi.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUserId(String userId);

    void deleteById(Long id);

    //해든
    Optional<Like> findByUserIdAndAlbumId(String userId, String albumId);

    Optional<Like> findByAlbumId(String albumId);

    boolean existsByUserIdAndAlbumId(String userId, String albumId);

    void deleteByUserIdAndAlbumId(String userId, String albumId);
    void deleteByAlbumId(String albumId);

}
