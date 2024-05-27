package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.MiTi.MiTi.service.AlbumService;

import java.util.List;

@Controller
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    public String getMainPage(Model model) {
        List<String> albumDetails = albumService.getAllAlbumDetails();
        model.addAttribute("albumDetails", albumDetails);
        return "album/main_list";
    }

    @GetMapping("/album/{albumDetail}")
    public String getAlbumDetail(@PathVariable String albumDetail, Model model) {
        List<Album> albums = albumService.getAlbumsByDetail(albumDetail);
        model.addAttribute("albums", albums);
        return "album/album_detail";
    }

}
