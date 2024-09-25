package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.PlaylistRepository;
import com.MiTi.MiTi.service.PlaylistService;
import com.MiTi.MiTi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.StringHelper.join;

@Controller
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(PlaylistController.class); // Logger 선언

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


        // 추천알고리즘

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

    @GetMapping("/playlist/detail/{mood}")
    public String getPlaylistDetail(@PathVariable("mood") String mood, Model model) {
        List<Album> playlist = playlistService.generatePlaylistByMood(mood);

        if (playlist.isEmpty()) {
            return "error"; // 재생목록이 비어 있을 경우 처리
        }

        // 곡 개수 및 총 재생 시간 계산
        int totalSongs = playlist.size();
        int totalDurationMs = playlist.stream().mapToInt(Album::getMusic_duration_ms).sum();
        String totalDuration = String.format("%d:%02d", totalDurationMs / 60000, (totalDurationMs % 60000) / 1000);

        // 각 곡의 분과 초를 미리 계산하여 모델에 추가
        List<String> formattedDurations = playlist.stream()
                .map(song -> {
                    long durationMs = song.getMusic_duration_ms();
                    long minutes = durationMs / 60000;
                    long seconds = (durationMs % 60000) / 1000;
                    return String.format("%d:%02d", minutes, seconds);
                })
                .collect(Collectors.toList());

        model.addAttribute("playlist", playlist);
        model.addAttribute("formattedDurations", formattedDurations);  // 미리 계산된 재생 시간을 전달
        model.addAttribute("totalSongs", totalSongs);
        model.addAttribute("totalDuration", totalDuration);

        // entry 객체 추가
        Map<String, String> entry = new HashMap<>();
        entry.put("key", mood); // key 값을 mood로 설정
        model.addAttribute("entry", entry);

        return "playlist_detail";
    }

    @GetMapping("/playlist_detail")
    public String showPlaylistDetail(Model model, @AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            model.addAttribute("user", user);
            log.debug("User ID: " + user.getAttribute("id"));
        } else {
            log.debug("User is null");
        }
        return "playlist_detail";
    }

}
