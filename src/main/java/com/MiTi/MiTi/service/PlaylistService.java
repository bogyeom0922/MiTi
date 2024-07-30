package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.entity.Playlist;
import com.MiTi.MiTi.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
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
                        .music_name(playlist.getAlbum().getMusic_name())
                        .music_artist_name(playlist.getAlbum().getMusic_artist_name())
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
                    .music_name(playlist.getAlbum().getMusic_name())
                    .album_image(playlist.getAlbum().getAlbum_image())
                    .music_artist_name(playlist.getAlbum().getMusic_artist_name())
                    .build();
            playlistDtoList.add(playlistDto);
        }
        return playlistDtoList;
    }
}
