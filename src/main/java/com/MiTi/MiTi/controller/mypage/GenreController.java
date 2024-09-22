package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.GenreDto;
import com.MiTi.MiTi.repository.GenreRepository;
import com.MiTi.MiTi.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("genreList", genreDtoList);
        model.addAttribute("userId", userId);
        return "mypage/mypage_genre";
    }

    // 선호장르 삭제 (물리적 삭제)

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

    //선호장르 선택

    @PostMapping("/mypage/genre/add")
    @ResponseBody
    public String addGenre(@RequestBody GenreDto genreDto) {
        try {
            genreService.addGenre(genreDto);
            return "success";
        } catch (Exception e) {
            return "failure";
        }
    }

    @GetMapping("/mypage/genre/non-selected/{userId}")
    @ResponseBody
    public List<GenreDto> getNonSelectedGenres(@PathVariable String userId) {
        return genreService.getNonSelectedGenres(userId);
    }
}
