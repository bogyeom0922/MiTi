package com.MiTi.MiTi.repository;


import com.MiTi.MiTi.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByProviderId(String providerId);

    void deleteById(Long id);

    //해든
    Optional<Like> findByProviderIdAndAlbumId(String providerId, Long albumId);

    Optional<Like> findByAlbumId(Long albumId);

    boolean existsByProviderIdAndAlbumId(String providerId, Long albumId);

    void deleteByProviderIdAndAlbumId(String providerId, Long albumId);
    void deleteByAlbumId(Long albumId);

}
