package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 사용자 ID와 앨범 ID로 좋아요 존재 여부 확인
    boolean existsByUserIdAndAlbumId(String userId, String albumId);

    // 사용자 ID와 앨범 ID로 좋아요 레코드 조회
    Optional<Like> findByUserIdAndAlbumId(String userId, String albumId);

    // 사용자 ID로 좋아요 목록 조회
    List<Like> findByUserId(String userId);

    // 좋아요 ID로 삭제
    void deleteById(Long id);
}
