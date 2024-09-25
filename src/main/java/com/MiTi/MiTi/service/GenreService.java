package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.GenreDto;
import com.MiTi.MiTi.entity.Genre;
import com.MiTi.MiTi.repository.GenreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                    .genre_image(genre.getGenre_image())
                    .build();
            genreDtoList.add(genreDto);
        }
        return genreDtoList;

    }


    @Transactional
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

    @Transactional
    public List<GenreDto> getNonSelectedGenres(String userId) {
        List<Genre> allGenres = genreRepository.findAll();
        List<Genre> userGenres = genreRepository.findByUserId(userId);
        Set<String> userGenreNames = userGenres.stream()
                .map(Genre::getGenre)
                .collect(Collectors.toSet());

        Set<String> uniqueGenres = new HashSet<>();
        List<GenreDto> nonSelectedGenres = allGenres.stream()
                .filter(genre -> !userGenreNames.contains(genre.getGenre()) && uniqueGenres.add(genre.getGenre()))
                .map(genre -> GenreDto.builder()
                        .id(genre.getId())
                        .userId(userId)
                        .genre(genre.getGenre())
                        .genre_image(genre.getGenre_image())
                        .build())
                .collect(Collectors.toList());

        return nonSelectedGenres;
    }

    @Transactional
    public void addGenre(GenreDto genreDto) {
        Genre genre = Genre.builder()
                .userId(genreDto.getUserId())
                .genre(genreDto.getGenre())
                .genre_image(genreDto.getGenre_image())
                .build();
        genreRepository.save(genre);
    }

}