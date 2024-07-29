package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.MyComment;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.MemberRepository;
import com.MiTi.MiTi.repository.UserRepository;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.MyCommentService;
import com.MiTi.MiTi.service.MemberService;
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
    private MyCommentService myCommentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/album_list/{id}")
    public String albumList(Model model, @PathVariable Long id) {
        //album
        List<String> details = albumRepository.findAll()
                .stream()
                .map(Album::getDetail)
                .toList();

        // 중복을 제거하기 위해 Set으로 변환 후 다시 리스트로 변환
        List<String> uniqueDetails = details.stream()
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("details", uniqueDetails);

        //user
        UserDTO memberDTO = userService.findById(id);
        model.addAttribute("member", memberDTO);
        return "album/album_list";
    }

    @GetMapping("/album/{detail}/{id}")
    public String detailList(@PathVariable("detail") String detail, @PathVariable Long id,  Model model) {
        //album
        log.info(detail);
        List<Album> albums = albumService.findByDetail(detail);
        model.addAttribute("albums", albums);
        if (!albums.isEmpty()) {
            model.addAttribute("firstAlbum", albums.get(0));
        }

        //comment
        List<MyComment> myComments = myCommentService.comments(String.valueOf(albums.get(0).getId()));
        model.addAttribute("comments", myComments);

        //user
        UserDTO memberDTO = userService.findById(id);
        model.addAttribute("member", memberDTO);

        return "album/album_detail";
    }
}