package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // 상세 정보(detail)를 기준으로 앨범을 검색하는 메서드
    List<Album> findByDetail(String detail);

    // 음악 이름 또는 아티스트 이름으로 앨범 검색
    List<Album> findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(String musicName, String artistName);

    // 인기 순으로 앨범 검색
    @Query(value = "SELECT * FROM album ORDER BY music_popularity DESC", nativeQuery = true)
    List<Album> findAllByOrderByMusic_popularityDesc();

    // 장르 기반 앨범 검색 (GenreRec 테이블과 조인)
    @Query("SELECT a FROM album a JOIN GenreRec g ON a.id = g.albumId WHERE g.genre = :genre")
    List<Album> findAlbumsByGenre(@Param("genre") String genre);

    // 사용자 기록을 바탕으로 장르 추천 (Record와 GenreRec 조인)
    @Query("SELECT DISTINCT g.genre FROM GenreRec g JOIN Record r ON g.albumId = r.albumId WHERE r.providerId = :providerId")
    List<String> findGenresByUserRecord(@Param("providerId") String providerId);
}
