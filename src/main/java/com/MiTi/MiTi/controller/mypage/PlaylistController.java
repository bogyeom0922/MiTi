package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.RecordDto;
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

    @GetMapping("/mypage/playlist/{providerId}")
    public String list(@PathVariable String providerId, Model model) {
        List<PlaylistDto> playlistDtoList = playlistService.getPlaylistListByProviderId(String.valueOf(providerId));
        model.addAttribute("playlistList", playlistDtoList);


        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 사용자 정보를 모델에 추가

            return "mypage/mypage_playlist"; // 적절한 뷰 이름 반환
        }

        // 사용자 정보가 없는 경우, 적절한 처리를 해주셔야 합니다.
        return "error"; // 또는 다른 적절한 경로로 리디렉션
    }


    @GetMapping("/mypage/playlist/albums/{providerId}")
    public String getAlbumsByPlaylistName(@PathVariable String providerId, @RequestParam String userPlaylistName, Model model) {
        List<PlaylistDto> albumList = playlistService.getAlbumsByPlaylistName(userPlaylistName);
        model.addAttribute("albumList", albumList);

        model.addAttribute("userPlaylistName", userPlaylistName);

        Optional<UserDTO> userDTO = userService.getUserById(providerId);
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
