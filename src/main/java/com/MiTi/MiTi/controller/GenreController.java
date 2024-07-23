package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.GenreDto;
import com.MiTi.MiTi.repository.GenreRepository;
import com.MiTi.MiTi.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class GenreController {

    private final GenreService genreService;
    private final GenreRepository genreRepository;

    public GenreController(GenreService genreService, GenreRepository genreRepository) {
        this.genreService = genreService;
        this.genreRepository = genreRepository;
    }

    @GetMapping("/mypage/genre/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<GenreDto> genreDtoList = genreService.getGenreListByUserId(String.valueOf(userId));
        model.addAttribute("postList", genreDtoList);
        model.addAttribute("userId", userId);
        return "mypage_genre";
    }

    // 게시글 삭제 (물리적 삭제)

    @DeleteMapping("/mypage/genre/{id}")
    @ResponseBody
    public String deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);
            return "success";
        } catch (Exception e) {
            return "failure";
        }
    }
}
