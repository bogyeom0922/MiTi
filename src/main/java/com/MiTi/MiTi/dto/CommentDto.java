package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CommentDto {
    private Long Id; //id
    private String comment; //comment
    private String user_id; //user 테이블 id값
    private String album_id; //album 테이블 id값


    public static Object createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getComment(),
                comment.getUser_id(),
                comment.getAlbum_id()
        );
    }
}
