package com.MiTi.MiTi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {


    private String provider;  // OAuth2Provider 이름
    private String providerId; // OAuth2Provider에서 사용하는 ID
    private String name;
    private String email;
    private String image;
    private String role;

    // 확인용 getter
    public String getProviderId() {
        return providerId;
    }
}
