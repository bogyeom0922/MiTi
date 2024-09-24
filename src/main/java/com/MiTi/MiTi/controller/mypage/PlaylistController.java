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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.StringHelper.join;

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
    public String list(@PathVariable String userId, Model model) {
        List<PlaylistDto> playlistDtoList = playlistService.getPlaylistListByUserId(String.valueOf(userId));
        model.addAttribute("playlistList", playlistDtoList);
        model.addAttribute("userId", userId);

        Optional<UserDTO> userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);
        return "mypage/mypage_playlist";
    }

    @GetMapping("/mypage/playlist/albums/{userId}")
    public String getAlbumsByPlaylistName(@PathVariable String userId, @RequestParam String userPlaylistName, Model model) {
        List<PlaylistDto> albumList = playlistService.getAlbumsByPlaylistName(userPlaylistName);
        model.addAttribute("albumList", albumList);

        model.addAttribute("userPlaylistName", userPlaylistName);

        Optional<UserDTO> userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);

//        추천알고리즘
        List<Long> albumIds = albumList.stream()
                .map(PlaylistDto::getAlbumId)
                .collect(Collectors.toList());

        List<Long> recommendedAlbums = runPythonScript(albumIds);
        model.addAttribute("recommendedAlbums", recommendedAlbums);

        return "mypage/playlist_albums";
    }

    public List<Long> runPythonScript(List<Long> albumIds) {
        List<Long> results = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "/src/main/scripts/recommendation_algorithm.py");
            Process process = processBuilder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(join("\n", albumIds));
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Long line;
            while ((line = Long.valueOf(reader.readLine())) != null) {
                results.add(line);
            }
            reader.close();

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return results;
    }

//    <table class="my_table_playlist_recommended">
//    <tr th:each="album : ${recommendedAlbums}">
//        <td style="width: 9%">
//            <img th:src="@{${album.album_image}}" width="50" alt="앨범 이미지"/>
//        </td>
//        <td style="width: 50%" class="my_table_padding my_table_body">
//            <span th:text="${album.music_name}"></span>
//        </td>
//        <td style="width: 41%">
//            <span th:text="${album.music_artist_name}"></span>
//        </td>
//    </tr>
//</table>

}
