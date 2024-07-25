package com.MiTi.MiTi.entity;

import com.MiTi.MiTi.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK로 사용되는 id 필드는 Long 타입으로 설정

    @Column(name = "user_id", nullable = false, unique = true, length = 15)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Column(name = "user_mail", nullable = false, unique = true, length = 25)
    private String userMail;

    @Column(name = "user_pw", nullable = false, length = 100)
    private String userPw;

    @Builder
    public UserEntity(String userId, String userName, String userMail, String userPw) {
        this.userId = userId;
        this.userName = userName;
        this.userMail = userMail;
        this.userPw = userPw;
    }

    public static UserEntity fromDTO(UserDTO userDTO) {
        return UserEntity.builder()
                .userId(userDTO.getUserId())
                .userName(userDTO.getUserName())
                .userMail(userDTO.getUserMail())
                .userPw(userDTO.getUserPw())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
