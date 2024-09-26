package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.MyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyCommentRepository extends JpaRepository<MyComment, Long> {

    // 특정 앨범 댓글 조회
    List<MyComment> findByAlbumId(Long albumId);

    // 특정 사용자 댓글 조회
    List<MyComment> findByProviderId(String providerId);

    //댓글 삭제
    void deleteById(Long id);

}
