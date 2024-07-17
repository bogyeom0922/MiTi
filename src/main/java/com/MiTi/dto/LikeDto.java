
package com.MiTi.dto;

import com.MiTi.entity.Like;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LikeDto {

    private Integer user_id;
    private Integer album_id;
    private Boolean isLike;

    public Like toEntity(){
        return Like.builder()
                .user_id(user_id)
                .album_id(album_id)
                .isLike(isLike).build();
    }

    @Builder
    public LikeDto(Integer user_id, Integer album_id, Boolean isLike) {
        this.user_id = user_id;
        this.album_id = album_id;
        this.isLike = isLike;
    }

    public Integer getUserId() {
        return user_id;
    }

    public Integer getAlbumId() {
        return album_id;
    }
}
