package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.MemberDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.MyComment;
import com.MiTi.MiTi.repository.AlbumRepository;
import com.MiTi.MiTi.repository.MemberRepository;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.MemberService;
import com.MiTi.MiTi.service.MyCommentService;
//import com.MiTi.MiTi.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
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
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @GetMapping("/albums")
    public String home() {
        return "home";
    }

    /*
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
            Optional<MemberDTO> memberDTOOpt = memberService.findById(id);
            memberDTOOpt.ifPresent(memberDTO -> model.addAttribute("member", memberDTO));
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
            MemberDTO memberDTO = memberService.findById(id);
            model.addAttribute("member", memberDTO);

            return "album/album_detail";
        }
    */
    @GetMapping("/streaming/{albumId}")
    public String streaming(@PathVariable Long albumId, Model model) {
        Optional<Album> albumOpt = albumRepository.findById(albumId);

        if (albumOpt.isPresent()) {
            Album album = albumOpt.get();
            model.addAttribute("musicName", album.getMusic_name());
            model.addAttribute("musicUri", album.getMusic_uri());
            model.addAttribute("albumImage", album.getAlbum_image());
        }

        return "index"; // index.html로 이동
    }
}
