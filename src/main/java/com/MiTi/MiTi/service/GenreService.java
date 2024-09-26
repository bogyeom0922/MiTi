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
    public List<GenreDto> getGenreListByProviderId(String providerId) {
        List<Genre> genreList = genreRepository.findByProviderId(providerId);
        List<GenreDto> genreDtoList = new ArrayList<>();

        for (Genre genre : genreList) {
            GenreDto genreDto = GenreDto.builder()
                    .id(genre.getId())
                    .providerId(genre.getProviderId())
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
    public List<GenreDto> getNonSelectedGenres(String providerId) {
        List<Genre> allGenres = genreRepository.findAll();
        List<Genre> userGenres = genreRepository.findByProviderId(providerId);
        Set<String> userGenreNames = userGenres.stream()
                .map(Genre::getGenre)
                .collect(Collectors.toSet());

        Set<String> uniqueGenres = new HashSet<>();
        List<GenreDto> nonSelectedGenres = allGenres.stream()
                .filter(genre -> !userGenreNames.contains(genre.getGenre()) && uniqueGenres.add(genre.getGenre()))
                .map(genre -> GenreDto.builder()
                        .id(genre.getId())
                        .providerId(providerId)
                        .genre(genre.getGenre())
                        .genre_image(genre.getGenre_image())
                        .build())
                .collect(Collectors.toList());

        return nonSelectedGenres;
    }

    @Transactional
    public void addGenre(GenreDto genreDto) {
        Genre genre = Genre.builder()
                .providerId(genreDto.getProviderId())
                .genre(genreDto.getGenre())
                .genre_image(genreDto.getGenre_image())
                .build();
        genreRepository.save(genre);
    }

}