package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Genre;
import com.MiTi.MiTi.entity.Playlist;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.GenreRepository;
import com.MiTi.MiTi.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    public PlaylistService(PlaylistRepository playlistRepository, AlbumRepository albumRepository, GenreRepository genreRepository) {
        this.playlistRepository = playlistRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public List<PlaylistDto> getPlaylistListByProviderId(String providerId) {
        List<Playlist> playlistList = playlistRepository.findByProviderId(providerId);
        List<PlaylistDto> playlistDtoList = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();

        for (Playlist playlist : playlistList) {
            if (uniqueNames.add(playlist.getUserPlaylistName())) { // 중복이 아니면 추가
                PlaylistDto playlistDto = PlaylistDto.builder()
                        .id(playlist.getId())
                        .providerId(playlist.getProviderId())
                        .albumId(playlist.getAlbumId())
                        .userPlaylistName(playlist.getUserPlaylistName())
                        .userPlaylistImage(playlist.getUserPlaylistImage())
                        .build();
                playlistDtoList.add(playlistDto);
            }
        }
        return playlistDtoList;
    }

    @Transactional
    public List<PlaylistDto> getAlbumsByPlaylistName(String userPlaylistName) {
        List<Playlist> playlistList = playlistRepository.findByUserPlaylistName(userPlaylistName);
        List<PlaylistDto> playlistDtoList = new ArrayList<>();

        for (Playlist playlist : playlistList) {
            PlaylistDto playlistDto = PlaylistDto.builder()
                    .albumId(playlist.getAlbumId())
                    .userPlaylistName(playlist.getUserPlaylistName())
                    .music_name(playlist.getAlbum().getMusicName())
                    .album_image(playlist.getAlbum().getAlbum_image())
                    .music_artist_name(playlist.getAlbum().getMusicArtistName())
                    .build();
            playlistDtoList.add(playlistDto);
        }
        return playlistDtoList;
    }

    @Transactional
    public PlaylistDto getPlaylistDetails(String playlistId) {
        // playlistId가 null이거나 빈 문자열인지 확인
        if (playlistId == null || playlistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist ID is null or empty");
        }

        // String으로 받은 playlistId를 Long으로 변환
        Long parsedPlaylistId;
        try {
            parsedPlaylistId = Long.parseLong(playlistId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Playlist ID: " + playlistId, e);
        }

        // findById 메서드를 사용하여 플레이리스트를 가져옵니다.
        Optional<Playlist> playlistOptional = playlistRepository.findById(parsedPlaylistId);

        if (!playlistOptional.isPresent()) {
            throw new IllegalArgumentException("해당 플레이리스트를 찾을 수 없습니다: " + playlistId);
        }

        Playlist playlist = playlistOptional.get();
        PlaylistDto playlistDto = new PlaylistDto();

        // 재생 시간을 밀리초 단위로 합산
        int totalDuration = 0;
        List<Playlist> playlistList = playlistRepository.findByUserPlaylistName(playlist.getUserPlaylistName());

        for (Playlist song : playlistList) {
            totalDuration += song.getAlbum().getMusic_duration_ms();
        }

        // DTO에 필드 값을 설정
        playlistDto.setUserPlaylistImage(playlist.getAlbum().getAlbum_image());  // 플레이리스트 이미지 설정
        playlistDto.setTotalSongs(playlistList.size());  // 총 곡 수 설정 (int 값)
        playlistDto.setTotalDuration(totalDuration);  // 총 재생 시간 설정 (int 값)

        return playlistDto;
    }


    // 장르에 맞춰 추천 앨범을 가져오는 메서드 추가
    public List<Album> getRecommendedAlbumsByGenre(String genre) {
        return albumRepository.findAlbumsByGenre(genre);
    }

    // 유저가 선택한 장르에 맞춰 음향 특성을 분석한 앨범을 추천
    public Map<String, List<Album>> getRecommendedAlbumsByUserGenres(String providerId) {
        List<Genre> userGenres = genreRepository.findByProviderId(providerId);
        Map<String, List<Album>> recommendedAlbumsMap = new HashMap<>();

        for (Genre genre : userGenres) {
            List<Album> genreAlbums = albumRepository.findAlbumsByGenre(genre.getGenre());  // genre_rec 테이블과 매핑된 메서드
            if (!genreAlbums.isEmpty()) {
                recommendedAlbumsMap.put(genre.getGenre(), genreAlbums.stream().limit(20).collect(Collectors.toList()));
            }
        }

        return recommendedAlbumsMap;
    }
    // 사용자 기록을 바탕으로 추천 앨범을 가져오는 메서드 추가
    public List<Album> getRecommendedAlbumsBasedOnUserRecord(String providerId) {
        // 사용자가 들은 음악의 장르 목록을 가져옴
        List<String> userGenres = albumRepository.findGenresByUserRecord(providerId);

        // 각 장르별로 앨범을 추천
        List<Album> recommendedAlbums = new ArrayList<>();
        for (String genre : userGenres) {
            List<Album> genreAlbums = albumRepository.findAlbumsByGenre(genre);
            recommendedAlbums.addAll(genreAlbums);
        }

        // 중복 제거 및 최대 20곡으로 제한
        return recommendedAlbums.stream().distinct().limit(20).collect(Collectors.toList());
    }

    @Transactional
    public void save(Playlist playlist) {
        playlistRepository.save(playlist);
    }

    @Transactional
    public boolean deleteAlbumFromPlaylist(Long albumId, String userPlaylistName) {
        // 해당 플레이리스트에서만 앨범 삭제
        int deletedRows = playlistRepository.deleteByAlbumIdAndUserPlaylistName(albumId, userPlaylistName);
        return deletedRows > 0;  // 삭제가 정상적으로 이루어졌는지 확인
    }
}