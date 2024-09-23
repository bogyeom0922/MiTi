package com.MiTi.MiTi.OAuth2;

import com.MiTi.MiTi.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private User user;
    private String providerName;
    private String accessToken;

    public CustomOAuth2User(OAuth2UserInfo oAuth2UserInfo, User user, String accessToken) {
        super(List.of(new SimpleGrantedAuthority(user.getRole().name())), oAuth2UserInfo.getAttributes(),
                oAuth2UserInfo.getProvider().getAttributeKey());
        this.user = user;
        this.providerName = user.getId().getProvider().getProviderName();
        this.accessToken = accessToken;
    }

    @Override
    public String getName() {
        return user.getEmail(); // Return email as identifier
    }

    public String getProvider() {
        return providerName;
    }

    public String getAccessToken() {
        return accessToken;
    }
}