package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> { // Long -> String으로 변경
    // 상세 정보(detail)를 기준으로 앨범을 검색하는 메서드
    List<Album> findByDetail(String detail);

    // 음악 이름 또는 아티스트 이름으로 앨범 검색
    List<Album> findByMusicNameContainingIgnoreCaseOrMusicArtistNameContainingIgnoreCase(String musicName, String artistName);

    @Query(value = "SELECT * FROM album ORDER BY music_popularity DESC", nativeQuery = true)
    List<Album> findAllByOrderByMusic_popularityDesc();

    Page<Album> findAll(Pageable pageable);
    // 에너지와 댄서빌리티로 필터링
    @Query("SELECT a FROM album a WHERE a.music_energy > :energy AND a.music_danceability > :danceability")
    List<Album> findByEnergyAndDanceability(double energy, double danceability);

    // 밸런스와 어쿠스틱 특성으로 필터링
    @Query("SELECT a FROM album a WHERE a.music_valence < :valence AND a.music_acousticness > :acousticness")
    List<Album> findByValenceAndAcousticness(double valence, double acousticness);

    // 템포와 댄서빌리티로 필터링
    @Query("SELECT a FROM album a WHERE a.music_tempo > :tempo AND a.music_danceability > :danceability")
    List<Album> findByTempoAndDanceability(double tempo, double danceability);

    // 에너지와 소리 크기로 필터링
    @Query("SELECT a FROM album a WHERE a.music_energy > :energy AND a.music_loudness > :loudness")
    List<Album> findByEnergyAndLoudness(double energy, double loudness);

    // 어쿠스틱과 에너지로 필터링
    @Query("SELECT a FROM album a WHERE a.music_acousticness > :acousticness AND a.music_energy < :energy")
    List<Album> findByAcousticnessAndEnergy(double acousticness, double energy);
}
