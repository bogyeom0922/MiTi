package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.Genre;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class GenreDto {

    private Long id;
    private String userId;
    private String genre;
    private String genre_image;


    public Genre toEntity() {
        return Genre.builder()
                .id(id)
                .userId(userId)
                .genre(genre)
                .genre_image(genre_image)
                .build();
    }

    @Builder
    public GenreDto(Long id, String userId, String genre, String genre_image) {
        this.id=id;
        this.userId = userId;
        this.genre= genre;
        this.genre_image = genre_image;

    }


}
