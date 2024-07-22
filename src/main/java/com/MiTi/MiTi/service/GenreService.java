package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.GenreDto;
import com.MiTi.MiTi.entity.Genre;
import com.MiTi.MiTi.repository.GenreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional
    public List<GenreDto> getGenreListByUserId(String userId) {
        List<Genre> genreList = genreRepository.findByUserId(userId);
        List<GenreDto> genreDtoList = new ArrayList<>();

        for (Genre genre : genreList) {
            GenreDto genreDto = GenreDto.builder()
                    .id(genre.getId())
                    .userId(genre.getUserId())
                    .genre(genre.getGenre())
                    .build();
            genreDtoList.add(genreDto);
        }
        return genreDtoList;

    }


    @Transactional
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
