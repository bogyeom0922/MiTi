package com.MiTi.MiTi.dto;

import com.MiTi.MiTi.entity.UserEntity;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDTO {
    private Long id;
    private String userId;         // 아이디
    private String userName;       // 이름
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    private String userMail;      // 이메일
    private String userPw;        //비밀번호
    private String verificationCode; //인증코드 필드

    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setUserMail(userEntity.getUserMail());
        userDTO.setUserPw(userEntity.getUserPw());
        return userDTO;
    }
}
