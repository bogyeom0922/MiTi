package com.MiTi.MiTi.entity;


import java.io.Serializable;
import java.util.Objects;

public class RecordId implements Serializable {
    private Integer user_id;
    private Integer album_id;

    public RecordId() {}

    public RecordId(Integer user_id, Integer album_id) {
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
        RecordId recordId = (RecordId) o;
        return Objects.equals(user_id, recordId.user_id) && Objects.equals(album_id, recordId.album_id);
    }

    // Getters and setters
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

