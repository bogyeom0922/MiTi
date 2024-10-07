package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByProviderId(String providerId);
    List<Playlist> findByUserPlaylistName(String userPlaylistName); // 수정된 메서드 이름

    @Modifying
    @Query("DELETE FROM Playlist p WHERE p.albumId = :albumId AND p.userPlaylistName = :userPlaylistName")
    int deleteByAlbumIdAndUserPlaylistName(@Param("albumId") Long albumId, @Param("userPlaylistName") String userPlaylistName);
}