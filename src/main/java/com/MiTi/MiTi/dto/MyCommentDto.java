package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.MyComment;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MyCommentDto {
    private Long id; //id
    private String comment; //comment
    private String userId; //user 테이블 id값
    private String albumId; //album 테이블 id값
    private String music_name;
    private String album_image;
    private String music_artist_name;



    public MyComment toEntity() {
        return MyComment.builder()
                .id(id)
                .userId(userId)
                .albumId(albumId)
                .comment(comment)
                .build();
    }

    @Builder
    public MyCommentDto(Long id, String userId, String albumId, String comment, String music_name, String album_image, String music_artist_name) {
        this.id=id;
        this.userId = userId;
        this.albumId = albumId;
        this.comment = comment;
        this.music_name = music_name;
        this.album_image = album_image;
        this.music_artist_name = music_artist_name;
    }

}
