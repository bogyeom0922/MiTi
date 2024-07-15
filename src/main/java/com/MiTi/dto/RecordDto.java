package com.MiTi.dto;


import com.MiTi.entity.Record;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecordDto {

    private Integer user_id;
    private Integer album_id;

    public Record toEntity(){
        return Record.builder()
                .user_id(user_id)
                .album_id(album_id).build();

    }

    @Builder
    public RecordDto(Integer user_id, Integer album_id) {
        this.user_id = user_id;
        this.album_id = album_id;
    }


}
