package com.MiTi.MiTi.controller.album;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.CommentService;
import com.MiTi.MiTi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumRepository albumRepository;
    private final CommentService commentService;
    private final UserService userService;

    public AlbumController(AlbumService albumService, AlbumRepository albumRepository,
                           CommentService commentService, UserService userService) {
        this.albumService = albumService;
        this.albumRepository = albumRepository;
        this.commentService = commentService;
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
    public String detailList(@PathVariable("detail") String detail, @PathVariable ("id") Long id, Model model) {
        log.info("Detail: {}", detail);

        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);

        if (!albums.isEmpty()) {
            model.addAttribute("firstAlbum", albums.get(0));

            List<Comment> myComments = commentService.comments(String.valueOf(albums.get(0).getId()));
            model.addAttribute("comments", myComments);
        }

        UserDTO userDTO = userService.getUserById(id);
        model.addAttribute("user", userDTO);

        UserDTO memberDTO = userService.getUserById(id);
        model.addAttribute("member", memberDTO);

        return "album/album_detail";
    }
}