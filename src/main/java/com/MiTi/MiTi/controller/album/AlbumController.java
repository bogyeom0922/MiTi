package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Playlist;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumRepository albumRepository;
    private final CommentService commentService;
    private final UserService userService;
    private final LikeService likeService;
    private final PlaylistService playlistService;

    public AlbumController(AlbumService albumService, AlbumRepository albumRepository,
                           CommentService commentService, LikeService likeService, UserService userService, PlaylistService playlistService) {
        this.albumService = albumService;
        this.albumRepository = albumRepository;
        this.commentService = commentService;
        this.likeService = likeService;
        this.userService = userService;
        this.playlistService = playlistService;
    }

    @GetMapping("/album/{detail}/{providerId}")
    public String detailList(@PathVariable("detail") String detail, @PathVariable(value = "providerId") String providerId, Model model) {
        log.info("Detail: {}", detail);

        // 앨범과 곡 목록 가져오기
        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);

        if (!albums.isEmpty()) {
            model.addAttribute("firstAlbum", albums.get(0));

            // 각 곡에 대한 좋아요 상태를 설정 (사용자별)
            if (providerId != null) {
                for (Album album : albums) {
                    boolean isLiked = likeService.isAlbumLikedByUser(providerId, album.getId());
                    album.setIsLiked(isLiked); // 앨범 객체에 좋아요 상태 설정
                }

                // 첫 번째 앨범에 대한 좋아요 상태 설정
                boolean isLikedAlbum = likeService.isAlbumLikedByUser(providerId, albums.get(0).getId());
                model.addAttribute("isLikedAlbum", isLikedAlbum);
            }

        }

        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 사용자 정보를 모델에 추가

            List<PlaylistDto> playlistDtoList = playlistService.getPlaylistListByProviderId(String.valueOf(providerId));
            model.addAttribute("playlists", playlistDtoList); // 재생 목록을 모델에 추가
        }

        return "album/album_detail"; // 또는 다른 적절한 경로로 리디렉션
    }

    @PostMapping("/playlist/add")
    @ResponseBody
    public ResponseEntity<String> addPlaylist(@RequestBody PlaylistDto playlistDto) {
        try {
            // user_playlist_image 기본값 설정
            String defaultImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSer3RBirIRkkFqpY3Fp2YfHANEPmafXSt7IA&s";

            // Playlist 엔티티 생성 및 저장
            Playlist playlist = Playlist.builder()
                    .providerId(playlistDto.getProviderId()) // PlaylistDto의 providerId
                    .albumId(playlistDto.getAlbumId()) // PlaylistDto의 albumId
                    .userPlaylistName(playlistDto.getUserPlaylistName()) // PlaylistDto의 userPlaylistName
                    .userPlaylistImage(defaultImageUrl) // 기본 이미지 URL 설정
                    .build();

            // 저장
            playlistService.save(playlist);

            return ResponseEntity.ok("재생 목록에 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    @PostMapping("/playlist/create")
    @ResponseBody
    public ResponseEntity<String> createPlaylist(@RequestBody PlaylistDto playlistDto) {
        try {
            // user_playlist_image 기본값 설정
            String defaultImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSer3RBirIRkkFqpY3Fp2YfHANEPmafXSt7IA&s";

            // Playlist 엔티티 생성 및 저장
            Playlist playlist = Playlist.builder()
                    .providerId(playlistDto.getProviderId()) // PlaylistDto의 providerId
                    .albumId(playlistDto.getAlbumId())
                    .userPlaylistName(playlistDto.getUserPlaylistName()) // PlaylistDto의 userPlaylistName
                    .userPlaylistImage(defaultImageUrl) // 기본 이미지 URL 설정
                    .build();

            // 저장
            playlistService.save(playlist);

            // 로그 추가
            System.out.println("Received providerId: " + playlistDto.getProviderId());
            System.out.println("Received albumId: " + playlistDto.getAlbumId());
            System.out.println("Received userPlaylistName: " + playlistDto.getUserPlaylistName());

            return ResponseEntity.ok("재생 목록이 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    //스트리밍에 필요함
    @GetMapping("api/music/{id}")
    public ResponseEntity<AlbumDto> getMusicById(@PathVariable Long id) {
        AlbumDto albumDto = albumService.getAlbumDtoById(id);
        if (albumDto != null) {
            return ResponseEntity.ok(albumDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/album/list")
    public String getAlbumList(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 6;  // 한 페이지에 6개의 앨범만 표시
        Pageable pageable = (Pageable) PageRequest.of(page, pageSize);  // 페이지네이션 설정
        Page<AlbumDto> albumPage = albumService.getAlbumList(pageable);  // 페이지네이션된 앨범 리스트 가져오기

        model.addAttribute("albumList", albumPage.getContent());  // 현재 페이지의 앨범 리스트 추가
        model.addAttribute("totalPages", albumPage.getTotalPages());  // 총 페이지 수 추가
        model.addAttribute("currentPage", page);  // 현재 페이지 번호 추가

        return "albumList";  // albumList.html로 이동
    }




}