package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.GenreDto;
import com.MiTi.MiTi.dto.PlaylistDto;
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

    @GetMapping("/mypage/genre/{providerId}")
    public String list(@PathVariable String providerId, Model model) {
        List<GenreDto> genreDtoList = genreService.getGenreListByProviderId(String.valueOf(providerId));
        model.addAttribute("genreList", genreDtoList);


        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 사용자 정보를 모델에 추가

            return "mypage/mypage_genre"; // 적절한 뷰 이름 반환
        }

        // 사용자 정보가 없는 경우, 적절한 처리를 해주셔야 합니다.
        return "error"; // 또는 다른 적절한 경로로 리디렉션
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

    @GetMapping("/mypage/genre/non-selected/{providerId}")
    @ResponseBody
    public List<GenreDto> getNonSelectedGenres(@PathVariable String providerId) {
        return genreService.getNonSelectedGenres(providerId);
    }
}