package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.repository.PlaylistRepository;
import com.MiTi.MiTi.service.PlaylistService;
import com.MiTi.MiTi.service.UserService;
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
    private final UserService userService;

    public PlaylistController(PlaylistService playlistService, PlaylistRepository playlistRepository, UserService userService) {
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    @GetMapping("/mypage/playlist/{userId}")
    public String list(@PathVariable String userId, Model model, @PathVariable ("userId") Long user) {
        List<PlaylistDto> playlistDtoList = playlistService.getPlaylistListByUserId(String.valueOf(userId));
        model.addAttribute("postList", playlistDtoList);
        model.addAttribute("userId", userId);

        UserDTO userDTO = userService.getUserById(user);
        model.addAttribute("user", userDTO);
        return "mypage/mypage_playlist";
    }

    @GetMapping("/mypage/playlist/albums/{userId}")
    public String getAlbumsByPlaylistName(@RequestParam String userPlaylistName, Model model, @PathVariable ("userId") Long user) { // 수정된 파라미터 이름
        List<PlaylistDto> albumList = playlistService.getAlbumsByPlaylistName(userPlaylistName);
        model.addAttribute("albumList", albumList);

        // 선택한 플레이리스트 이름 모델 추가
        model.addAttribute("userPlaylistName", userPlaylistName);

        UserDTO userDTO = userService.getUserById(user);
        model.addAttribute("user", userDTO);
        return "mypage/playlist_albums";
    }

}
