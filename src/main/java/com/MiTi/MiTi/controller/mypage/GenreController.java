package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.GenreDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.repository.GenreRepository;
import com.MiTi.MiTi.service.GenreService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class GenreController {

    private final GenreService genreService;
    private final GenreRepository genreRepository;
    private final UserService userService;

    public GenreController(GenreService genreService, GenreRepository genreRepository, UserService userService) {
        this.genreService = genreService;
        this.genreRepository = genreRepository;
        this.userService = userService;
    }

    @GetMapping("/mypage/genre/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<GenreDto> genreDtoList = genreService.getGenreListByUserId(String.valueOf(userId));
        model.addAttribute("genreList", genreDtoList);
        model.addAttribute("userId", userId);

        Optional<UserDTO> userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);
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
