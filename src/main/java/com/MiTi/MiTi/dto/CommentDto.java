package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Comment;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CommentDto {
    private Long Id; //id
    private String comment; //comment
    private String providerId; //user 테이블 id값
    private Long albumId; //album 테이블 id값

    public static CommentDto createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getComment(),
                comment.getProviderId(),
                comment.getAlbumId()
        );
    }
}