package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.AlbumDto;
import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.CommentService;
import com.MiTi.MiTi.service.LikeService;
import com.MiTi.MiTi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumRepository albumRepository;
    private final CommentService commentService;
    private final UserService userService;
    private final LikeService likeService;

    public AlbumController(AlbumService albumService, AlbumRepository albumRepository,
                           CommentService commentService, LikeService likeService, UserService userService) {
        this.albumService = albumService;
        this.albumRepository = albumRepository;
        this.commentService = commentService;
        this.likeService = likeService;
        this.userService = userService;
    }

    @GetMapping("/album_list/{id}")
    public String albumList(@PathVariable("id") Long userId, Model model) {
        List<String> details = albumRepository.findAll()
                .stream()
                .map(Album::getDetail)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("details", details);

       //UserDTO userDTO = userService.getUserById(userId);
        //model.addAttribute("user", userDTO);

        return "album/album_list";
    }

    @GetMapping("/album/{detail}/{id}")
    public String detailList(@PathVariable("detail") String detail, @PathVariable("id") Long id, Model model, @RequestParam(value = "userId", required = false) String userId) {
        log.info("Detail: {}", detail);

        // 앨범과 곡 목록 가져오기
        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);

        if (!albums.isEmpty()) {
            model.addAttribute("firstAlbum", albums.get(0));

            // 각 곡에 대한 좋아요 상태를 설정 (사용자별)
            if (userId != null) {
                for (Album album : albums) {
                    boolean isLiked = likeService.isAlbumLikedByUser(userId, String.valueOf(album.getId()));
                    album.setIsLiked(isLiked); // 앨범 객체에 좋아요 상태 설정
                }

                // 첫 번째 앨범에 대한 좋아요 상태 설정
                boolean isLikedAlbum = likeService.isAlbumLikedByUser(userId, String.valueOf(albums.get(0).getId()));
                model.addAttribute("isLikedAlbum", isLikedAlbum);
            }

        }

        //UserDTO userDTO = userService.getUserById(id);
        //model.addAttribute("user", userDTO);

        return "album/album_detail";
    }

    //스트리밍에 필요함
    @GetMapping("api/music/{id}")
    public ResponseEntity<AlbumDto> getMusicById(@PathVariable String id) {
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