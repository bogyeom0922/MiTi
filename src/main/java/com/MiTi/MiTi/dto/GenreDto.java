package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Genre;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class GenreDto {

    private Long id;
    private String providerId;
    private String genre;
    private String genre_image;


    public Genre toEntity() {
        return Genre.builder()
                .id(id)
                .providerId(providerId)
                .genre(genre)
                .genre_image(genre_image)
                .build();
    }

    @Builder
    public GenreDto(Long id, String providerId, String genre, String genre_image) {
        this.id=id;
        this.providerId = providerId;
        this.genre= genre;
        this.genre_image = genre_image;

    }


}
