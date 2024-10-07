package com.MiTi.MiTi.controller.user;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.service.AlbumService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FindController {

    @Autowired
    private AlbumService albumService;

    @GetMapping("/find")
    public String findMusic(@RequestParam(value = "query", required = false, defaultValue = "") String query, Model model) {
        // 앨범 검색
        List<Album> albums = albumService.findByMusicNameOrArtistName(query, query);
        model.addAttribute("albums", albums);
        model.addAttribute("query", query);
        return "play/find";
    }


    @GetMapping("/find/autocomplete")
    @ResponseBody
    public String autocomplete(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        List<Album> albums = albumService.findByMusicNameOrArtistName(query, query);
        StringBuilder sb = new StringBuilder();

        // 현재 로그인한 사용자의 providerId 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String providerId = null;

        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) auth.getPrincipal();
            providerId = oauthUser.getAttribute("id"); // 고유 ID를 가져옴
        }
        for (Album album : albums) {
            sb.append("<div class='autocomplete-item'>")
                    .append("<a href=\"/album/") // 앨범 상세 페이지로의 링크
                    .append(album.getDetail()) // 앨범 ID로 변경
                    .append("/") // 슬래시 추가
                    .append(providerId) // 유저 ID 추가
                    .append("\">")
                    .append("<p>")
                    .append(album.getMusicName())
                    .append(" - ")
                    .append(album.getMusicArtistName())
                    .append("</p>")
                    .append("</a>")
                    .append("</div>");
        }
        return sb.toString();
    }

    @GetMapping("/find/album/{id}/{providerId}")
    public String getAlbumDetail(@PathVariable Long id, @PathVariable String providerId, Model model) {
        Album album = albumService.findById(id); // ID로 앨범 조회
        model.addAttribute("firstAlbum", album);
        model.addAttribute("providerId", providerId); // providerId를 모델에 추가
        return "album/album_detail"; // 앨범 상세 페이지 템플릿 이름
    }
}