package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.entity.Playlist;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

            List<Comment> myComments = commentService.comments(albums.get(0).getId());
            model.addAttribute("comments", myComments);

            // 각 곡에 대한 좋아요 상태를 설정 (사용자별)
            if (providerId != null) {
                for (Album album : albums) {
                    boolean isLiked = likeService.isAlbumLikedByUser(providerId, album.getId());
                    album.setIsLiked(isLiked); // 앨범 객체에 좋아요 상태 설정
                }

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

    //토글 아코디언
    @GetMapping("/api/playlists")
    @ResponseBody
    public List<PlaylistDto> getPlaylistsByProvider(@RequestParam String providerId) {
        return playlistService.getPlaylistListByProviderId(providerId);
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


    @GetMapping("/api/streaming/{providerId}/{albumId}")
    public ResponseEntity<List<AlbumDto>> getSimilarAlbums(@PathVariable("providerId") String providerId, @PathVariable("albumId") Long albumId) {
        // 유저 확인
        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (!userDTOOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 유저가 없을 경우 404
        }

        try {
            // 파이썬 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python3", "/home/ec2-user/mititest/src/main/scripts/recommendation_algorithm.py", albumId.toString());
            Process process = pb.start();

            // 파이썬 스크립트의 출력을 읽음
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String result = reader.lines().collect(Collectors.joining());
            String errorOutput = errorReader.lines().collect(Collectors.joining());

            // 에러 출력을 로그에 기록
            if (!errorOutput.isEmpty()) {
                System.err.println("Python script error output: " + errorOutput);
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<AlbumDto> albumDtos = mapper.readValue(result, new TypeReference<List<AlbumDto>>() {});


            // 응답 헤더에 Content-Type 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");


            // AlbumDto 리스트가 비어 있는 경우 204 반환
            if (albumDtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // AlbumDto 리스트를 Album 리스트로 변환
            List<Album> streaminglist = albumDtos.stream()
                    .map(dto -> new Album(dto.getId(), dto.getMusicName(), dto.getAlbumImage(), dto.getMusicArtistName(), dto.getMusic_duration_ms(), dto.getMusic_uri(), dto.getDetail()))
                    .collect(Collectors.toList());

            // Album 리스트를 AlbumDto 리스트로 변환
            List<AlbumDto> playlistDto = streaminglist.stream()
                    .map(album -> new AlbumDto(
                            album.getId(),
                            album.getMusicName(),
                            album.getAlbum_image(),
                            album.getMusicArtistName(),
                            album.getMusic_duration_ms(),
                            album.getMusic_uri(),
                            album.getDetail()
                    ))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(playlistDto,headers, HttpStatus.OK);  // JSON 형태로 추천 앨범 리스트 반환

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    //앨범 전체 스트리밍
    @GetMapping("/api/album/{providerId}/{detail}")
    public ResponseEntity<List<AlbumDto>> getAllAlbumList(@PathVariable("providerId") String providerId, @PathVariable("detail") String detail) {
        // 장르 기반으로 플레이리스트 생성
        List<Album> albumlist = albumService.findByDetail(detail);

        // 유저 정보 확인
        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (!userDTOOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 유저가 없으면 404 반환
        }

        if (albumlist.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 재생 목록이 비어 있으면 204 반환
        }

        // Album 객체를 AlbumDto로 변환 (필요한 필드만 포함)
        List<AlbumDto> playlistDto = albumlist.stream()
                .map(album -> new AlbumDto(
                        album.getId(),
                        album.getMusicName(),
                        album.getAlbum_image(),
                        album.getMusicArtistName(),
                        album.getMusic_duration_ms(),
                        album.getMusic_uri(),
                        album.getDetail()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(playlistDto);  // JSON 형태로 플레이리스트 반환
    }


}
