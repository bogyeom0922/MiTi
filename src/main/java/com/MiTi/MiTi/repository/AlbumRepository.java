package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> { // Long -> String으로 변경
    // 상세 정보(detail)를 기준으로 앨범을 검색하는 메서드
    List<Album> findByDetail(String detail);

    // 음악 이름 또는 아티스트 이름으로 앨범 검색
    List<Album> findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(String musicName, String artistName);

    @Query(value = "SELECT * FROM album ORDER BY music_popularity DESC", nativeQuery = true)
    List<Album> findAllByOrderByMusic_popularityDesc();

}

