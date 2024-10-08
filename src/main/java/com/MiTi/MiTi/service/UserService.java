package com.MiTi.MiTi.service;

import com.MiTi.MiTi.OAuth2.OAuth2Provider;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.User;
import com.MiTi.MiTi.entity.UserId;
import com.MiTi.MiTi.entity.RoleType;
import com.MiTi.MiTi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 정보 가져오기
    public Optional<UserDTO> getUserById(String providerId) {
        System.out.println("Provider ID: " + providerId); // 사용자 ID 확인
        try {
            // 고정된 provider 값 사용
            OAuth2Provider oAuth2Provider = OAuth2Provider.SPOTIFY; // 예시로 SPOTIFY 사용
            UserId userId = UserId.builder()
                    .provider(oAuth2Provider)  // 고정된 provider
                    .providerId(providerId)
                    .build();

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserDTO userDTO = UserDTO.builder()
                        .provider(user.getId().getProvider().name())
                        .providerId(user.getId().getProviderId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .image(user.getImage())
                        .role(user.getRole().name())
                        .build();
                return Optional.of(userDTO);
            }
        } catch (IllegalArgumentException e) {
            // provider가 유효하지 않을 때 예외 처리
            System.out.println("Invalid provider");
        }
        return Optional.empty();
    }

    // 회원 정보 저장하기
    public UserDTO saveUser(UserDTO userDTO) {
        UserId userId = UserId.builder()
                .provider(OAuth2Provider.valueOf(userDTO.getProvider())) // OAuth2Provider 열거형 값
                .providerId(userDTO.getProviderId())
                .build();

        User user = User.builder()
                .id(userId)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .image(userDTO.getImage())
                .role(RoleType.valueOf(userDTO.getRole()))
                .build();

        User savedUser = userRepository.save(user); // 저장
        return convertToUserDTO(savedUser); // User 엔티티를 UserDTO로 변환
    }

    // User 엔티티를 UserDTO로 변환하는 메서드
    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .provider(user.getId().getProvider().name())
                .providerId(user.getId().getProviderId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .role(user.getRole().name())
                .build();
    }

    // OAuth2Provider 값을 고정시키는 메서드 (예시로 SPOTIFY 사용)
    private OAuth2Provider getFixedOAuth2Provider() {
        return OAuth2Provider.SPOTIFY; // 실제 구현에 맞게 수정 가능
    }
}
