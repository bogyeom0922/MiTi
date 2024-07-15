package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //특정 앨범 댓글 조회
    List<Comment> findByAlbumId(String albumId);
    //특정 사용자 댓글 조회
    List<Comment> findByUserId(String userId);

}
