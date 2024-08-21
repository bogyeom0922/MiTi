package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByDetail(String detail);
    // 음악 제목 또는 아티스트 이름으로 검색
    List<Album> findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(String musicName, String artistName);
}
