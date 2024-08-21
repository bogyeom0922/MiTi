package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.Comment;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.CommentService;
import com.MiTi.MiTi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired

    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/album_list/{id}")
    public String albumList(@PathVariable("id") Long userId, Model model) {
        // 앨범 상세 정보 목록 조회
        List<String> details = albumRepository.findAll()
                .stream()
                .map(Album::getDetail)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("details", details);


        //user
        UserDTO memberDTO = userService.getUserById(id);

        model.addAttribute("member", memberDTO);

        return "album/album_list";
    }

    @GetMapping("/album/{detail}/{id}")
/*
    public String detailList(@PathVariable("detail") String detail,
                             @PathVariable("id") Long userId, Model model) {
        // 앨범 상세 정보 조회
        log.info("Detail: {}", detail);
*/
    public String detailList(@PathVariable("detail") String detail, @PathVariable Long id, Model model) {
        //album
        log.info(detail);

        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);

        if (!albums.isEmpty()) {
            model.addAttribute("firstAlbum", albums.get(0));


            // 댓글 조회
            List<Comment> myComments = commentService.comments(String.valueOf(albums.get(0).getId()));
            model.addAttribute("comments", myComments);
        }

        // 사용자 정보 조회
        UserDTO userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);


        return "album/album_detail";
    }
}
