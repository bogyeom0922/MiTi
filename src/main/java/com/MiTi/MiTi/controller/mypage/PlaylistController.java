package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.PlaylistRepository;
import com.MiTi.MiTi.service.LikeService;
import com.MiTi.MiTi.service.PlaylistService;
import com.MiTi.MiTi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final LikeService likeService;

    private static final Logger log = LoggerFactory.getLogger(PlaylistController.class); // Logger 선언

    public PlaylistController(PlaylistService playlistService, PlaylistRepository playlistRepository, UserService userService, LikeService likeService) {
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
        this.userService = userService;
        this.likeService = likeService;
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

        // Optional 처리
        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO);
        } else {
            return "error"; // 유저가 존재하지 않을 경우 에러 페이지로 이동
        }

/*        추천알고리즘
        List<Long> albumIds = albumList.stream()
                .map(PlaylistDto::getAlbumId)
                .collect(Collectors.toList());

        List<Long> recommendedAlbums = runPythonScript(albumIds);
        model.addAttribute("recommendedAlbums", recommendedAlbums);
*/
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

    @GetMapping("/playlist/detail/{providerId}/{genre}")
    public String getPlaylistDetail(@PathVariable("providerId") String providerId, @PathVariable("genre") String genre, Model model) {
        // 장르 기반으로 플레이리스트 생성
        List<Album> playlist = playlistService.getRecommendedAlbumsByGenre(genre);


        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO);
        } else {
            return "error"; // 유저가 존재하지 않을 경우 에러 페이지로 이동
        }

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

        // 좋아요 상태 설정
        if (providerId != null) {
            for (Album album : playlist) {
                boolean isLiked = likeService.isAlbumLikedByUser(providerId, album.getId());
                album.setIsLiked(isLiked); // 앨범 객체에 좋아요 상태 설정
            }

            // 첫 번째 앨범에 대한 좋아요 상태 설정
            boolean isLikedAlbum = likeService.isAlbumLikedByUser(providerId, playlist.get(0).getId());
            model.addAttribute("isLikedAlbum", isLikedAlbum);
        }

        model.addAttribute("playlist", playlist);
        model.addAttribute("formattedDurations", formattedDurations);  // 미리 계산된 재생 시간을 전달
        model.addAttribute("totalSongs", totalSongs);
        model.addAttribute("totalDuration", totalDuration);


        // entry 객체 추가
        Map<String, String> entry = new HashMap<>();
        entry.put("key", genre); // key 값을 genre로 설정
        model.addAttribute("entry", entry);
        model.addAttribute("providerId", providerId);  // providerId를 모델에 추가
        return "playlist_detail";
    }

    @GetMapping("/playlist_detail/{providerId}")
    public String showPlaylistDetail(@PathVariable("providerId") String providerId, Model model) {
        if (providerId != null && !providerId.isEmpty()) {
            model.addAttribute("providerId", providerId);
            log.debug("Provider ID: " + providerId);
        } else {
            log.error("Provider ID is missing or empty");
            return "error";  // 에러 페이지로 이동
        }

        return "playlist_detail";
    }

    @GetMapping("/mypage/playlist/record/{providerId}")
    public String getCustomizedPlaylist(@PathVariable String providerId, Model model) {
        // 사용자의 맞춤형 추천 앨범 가져오기
        List<Album> customizedAlbums = playlistService.getCustomizedAlbumsByUser(providerId);
        model.addAttribute("customizedAlbums", customizedAlbums);

        // providerId로 사용자 정보를 가져와서 모델에 추가
        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO);
        } else {
            return "error";  // 유저가 없을 경우 에러 페이지로 리디렉션
        }

        return "userplaylist_custom_songs";  // 맞춤형 추천 곡 템플릿
    }


}