package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
