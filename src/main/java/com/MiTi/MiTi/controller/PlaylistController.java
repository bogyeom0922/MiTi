package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.repository.PlaylistRepository;
import com.MiTi.MiTi.service.PlaylistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;

    public PlaylistController(PlaylistService playlistService, PlaylistRepository playlistRepository) {
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
    }

    @GetMapping("/mypage/playlist/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<PlaylistDto> playlistDtoList = playlistService.getPlaylistListByUserId(String.valueOf(userId));
        model.addAttribute("postList", playlistDtoList);
        model.addAttribute("userId", userId);
        return "mypage_playlist";
    }

    @GetMapping("/mypage/playlist/albums")
    public String getAlbumsByPlaylistName(@RequestParam String userPlaylistName, Model model) { // 수정된 파라미터 이름
        List<PlaylistDto> albumList = playlistService.getAlbumsByPlaylistName(userPlaylistName);
        model.addAttribute("albumList", albumList);
        return "playlist_albums";
    }
}
