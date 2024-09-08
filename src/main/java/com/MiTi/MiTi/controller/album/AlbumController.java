package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.CommentService;
import com.MiTi.MiTi.service.LikeService;
import com.MiTi.MiTi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

        UserDTO memberDTO = userService.getUserById(userId);
        model.addAttribute("member", memberDTO);

        return "album/album_list";
    }

    @GetMapping("/album/{detail}/{id}")
    public String detailList(@PathVariable("detail") String detail, @PathVariable("id") Long id, Model model, @RequestParam(value = "userId", required = false) String userId) {
        log.info("Detail: {}", detail);

        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);

        if (!albums.isEmpty()) {
            model.addAttribute("firstAlbum", albums.get(0));

            List<Comment> myComments = commentService.comments(String.valueOf(albums.get(0).getId()));
            model.addAttribute("comments", myComments);

            // 좋아요 상태 확인 코드 (로그인 체크 없이 동작)
            if (userId != null) {
                // albumId를 String으로 변환하여 전달
                boolean isLikedAlbum = likeService.isAlbumLikedByUser(userId, String.valueOf(albums.get(0).getId()));
                model.addAttribute("isLikedAlbum", isLikedAlbum);
            }
        }

        UserDTO userDTO = userService.getUserById(id);
        model.addAttribute("user", userDTO);

        UserDTO memberDTO = userService.getUserById(id);
        model.addAttribute("member", memberDTO);

        return "album/album_detail";
    }
}
