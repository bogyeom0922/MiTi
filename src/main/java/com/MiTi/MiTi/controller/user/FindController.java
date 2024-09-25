package com.MiTi.MiTi.controller.user;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

        for (Album album : albums) {
            sb.append("<div class='autocomplete-item'>")
                    .append("<p>")
                    .append(album.getMusicName())
                    .append(" - ")
                    .append(album.getMusicArtistName())
                    .append("</p>")
                    .append("</div>");
        }
        return sb.toString();
    }

    @GetMapping("/find/search")
    @ResponseBody
    public String searchMusic(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        List<Album> albums = albumService.findByMusicNameOrArtistName(query, query);
        StringBuilder sb = new StringBuilder();

        for (Album album : albums) {
            sb.append("<div>")
                    .append("<a href=\"/album/")
                    .append(album.getDetail())  // 앨범의 상세 정보 (이 부분이 필요하지 않다면 제거 가능)
                    .append("/").append(album.getId())  // 앨범 ID를 URL에 추가
                    .append("\">")
                    .append("<h3>")
                    .append(album.getMusicName())
                    .append("</h3>")
                    .append("<p>")
                    .append(album.getMusicArtistName())
                    .append("</p>")
                    .append("</a>")
                    .append("</div>");
        }
        return sb.toString();
    }
}