
package com.MiTi.entity;

import java.io.Serializable;
import java.util.Objects;

// @Entity 및 @Table 애노테이션 제거

public class LikeId implements Serializable {
    private Integer user_id;
    private Integer album_id;

    public LikeId() {}

    public LikeId(Integer user_id, Integer album_id) {
        this.user_id = user_id;
        this.album_id = album_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, album_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(user_id, likeId.user_id) && Objects.equals(album_id, likeId.album_id);
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Integer album_id) {
        this.album_id = album_id;
    }
}

