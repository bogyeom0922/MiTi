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


    public Genre toEntity() {
        return Genre.builder()
                .id(id)
                .userId(userId)
                .genre(genre)
                .build();
    }

    @Builder
    public GenreDto(Long id, String userId, String genre) {
        this.id=id;
        this.userId = userId;
        this.genre= genre;

    }


}
