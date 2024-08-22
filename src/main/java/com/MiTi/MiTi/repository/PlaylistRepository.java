package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(String userId);
    List<Playlist> findByUserPlaylistName(String userPlaylistName); // 수정된 메서드 이름
}
