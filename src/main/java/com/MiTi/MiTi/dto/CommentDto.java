package com.MiTi.MiTi.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long Id; //id
    private String comment; //comment
    private Long user_id; //user 테이블 id값
    private Long album_id; //album 테이블 id값
}
