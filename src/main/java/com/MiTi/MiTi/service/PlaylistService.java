package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Genre;
import com.MiTi.MiTi.entity.Playlist;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.GenreRepository;
import com.MiTi.MiTi.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, AlbumRepository albumRepository, GenreRepository genreRepository) {
        this.playlistRepository = playlistRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public List<PlaylistDto> getPlaylistListByUserId(String userId) {
        List<Playlist> playlistList = playlistRepository.findByUserId(userId);
        List<PlaylistDto> playlistDtoList = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();

        for (Playlist playlist : playlistList) {
            if (uniqueNames.add(playlist.getUserPlaylistName())) { // 중복이 아니면 추가
                PlaylistDto playlistDto = PlaylistDto.builder()
                        .id(playlist.getId())
                        .userId(playlist.getUserId())
                        .albumId(playlist.getAlbumId())
                        .userPlaylistName(playlist.getUserPlaylistName())
                        .userPlaylistImage(playlist.getUserPlaylistImage())
                        .album_image(playlist.getAlbum().getAlbum_image())
                        .music_name(playlist.getAlbum().getMusicName())
                        .music_artist_name(playlist.getAlbum().getMusicArtistName())
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
        // String으로 받은 playlistId를 Long으로 변환
        Long parsedPlaylistId = Long.parseLong(playlistId);

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

    // 유저가 선택한 장르에 맞춰 음향 특성을 분석한 앨범을 추천
    public Map<String, List<Album>> getRecommendedAlbumsByUserGenres(String userId) {
        List<Genre> userGenres = genreRepository.findByUserId(userId);
        Map<String, List<Album>> recommendedAlbumsMap = new HashMap<>();

        for (Genre genre : userGenres) {
            List<Album> genreAlbums = new ArrayList<>();
            switch (genre.getGenre()) {
                case "신나는":
                    genreAlbums = albumRepository.findByEnergyAndDanceability(0.7, 0.6);
                    break;
                case "울고싶어":
                    genreAlbums = albumRepository.findByValenceAndAcousticness(0.2, 0.7);
                    break;
                case "차에서":
                    genreAlbums = albumRepository.findByTempoAndDanceability(100, 0.5);
                    break;
                case "게임":
                    genreAlbums = albumRepository.findByEnergyAndLoudness(0.8, -5.0);
                    break;
                case "조용한":
                    genreAlbums = albumRepository.findByAcousticnessAndEnergy(0.8, 0.3);
                    break;
            }

            if (!genreAlbums.isEmpty()) {
                recommendedAlbumsMap.put(genre.getGenre(), genreAlbums.stream().limit(20).collect(Collectors.toList()));
            }
        }
        return recommendedAlbumsMap;
    }

    // 분위기에 따라 플레이리스트 생성
    public List<Album> generatePlaylistByMood(String mood) {
        List<Album> playlist = new ArrayList<>();

        switch (mood) {
            case "신나는":
                playlist = albumRepository.findByEnergyAndDanceability(0.7, 0.6);
                break;
            case "울고싶어":
                playlist = albumRepository.findByValenceAndAcousticness(0.2, 0.7);
                break;
            case "차에서":
                playlist = albumRepository.findByTempoAndDanceability(100, 0.5);
                break;
            case "게임":
                playlist = albumRepository.findByEnergyAndLoudness(0.8, -5.0);
                break;
            case "조용한":
                playlist = albumRepository.findByAcousticnessAndEnergy(0.8, 0.3);
                break;
            default:
                throw new IllegalArgumentException("잘못된 분위기 값입니다: " + mood);
        }

        return playlist.stream().limit(20).collect(Collectors.toList());
    }
}
