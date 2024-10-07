package com.MiTi.MiTi.entity;

import java.io.Serializable;
import java.util.Objects;

public class CustomizedRecId implements Serializable {
    private String userId;
    private Long albumId;

    // 기본 생성자
    public CustomizedRecId() {}

    // 매개변수가 있는 생성자
    public CustomizedRecId(String userId, Long albumId) {
        this.userId = userId;
        this.albumId = albumId;
    }

    // Getter, Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    // equals와 hashCode 메서드 오버라이드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomizedRecId that = (CustomizedRecId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(albumId, that.albumId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, albumId);
    }
}
