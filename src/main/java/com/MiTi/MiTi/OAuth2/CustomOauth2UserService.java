package com.MiTi.MiTi.OAuth2;

import com.MiTi.MiTi.entity.User;
import com.MiTi.MiTi.entity.UserId;
import com.MiTi.MiTi.entity.RoleType;
import com.MiTi.MiTi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // userRequest 는 code를 받아서 accessToken을 응답 받은 객체
    //OAuth2 인증 요청을 받아 사용자 정보를 로드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(userRequest, oAuth2User);
        return new CustomOAuth2User(
                oAuth2UserInfo,
                getOrCreateUser(oAuth2UserInfo),
                userRequest.getAccessToken().getTokenValue()
        );
    }


    //OAuth2 제공자의 이름을 확인하고, 해당 제공자에 맞는 OAuth2UserInfo 객체를 생성
    private OAuth2UserInfo getOAuth2UserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (registrationId.equals(OAuth2Provider.SPOTIFY.getProviderName())) {
            return new SpotifyUserInfo(oAuth2User.getAttributes());
        }
        return null;
    }


    //사용자 조회 및 생성
    private User getOrCreateUser(OAuth2UserInfo oAuth2UserInfo) {
        UserId userId = UserId.builder()
                .provider(oAuth2UserInfo.getProvider())
                .providerId(oAuth2UserInfo.getProviderId())
                .build();
        User existingUser = getExistingUser(userId);
        return existingUser != null ? existingUser : createUser(oAuth2UserInfo, userId);
    }


    //기존 사용자 조회
    private User getExistingUser(UserId userId) {
        Optional<com.MiTi.MiTi.entity.User> oUser = userRepository.findById(userId);
        return (User) oUser.orElse(null); // 회원이 없으면 null 반환
    }


    //새로운 사용자 정보를 생성하고 데이터베이스에 저장
    private User createUser(OAuth2UserInfo oAuth2UserInfo, UserId userId) {
        User user = User.builder()
                .id(userId)
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .role(RoleType.ROLE_USER)
                .image(oAuth2UserInfo.getImage())
                .build();
        return userRepository.save(user);
    }
}
