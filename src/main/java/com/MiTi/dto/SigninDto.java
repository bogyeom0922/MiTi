package com.MiTi.dto;

import com.MiTi.entity.Signin;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SigninDto {

    private Integer user_id;
    private String genre;

    public Signin toEntity() {
        return Signin.builder()
                .user_id(user_id)
                .genre(genre)
                .build();
    }

    @Builder
    public SigninDto(Integer user_id, String genre) {
        this.user_id = user_id;
        this.genre = genre;
    }
}
