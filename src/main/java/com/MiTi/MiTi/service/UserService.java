package com.MiTi.MiTi.service;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.UserEntity;
import com.MiTi.MiTi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public boolean idCheck(String userId) {
        logger.info("Checking if userId exists: {}", userId);
        return userRepository.existsByUserId(userId);
    }

    public boolean emailCheck(String userMail) {
        logger.info("Checking if email exists: {}", userMail);
        return userRepository.existsByUserMail(userMail);
    }

    @Transactional
    public synchronized String registerUser(UserDTO userDTO) {
        logger.info("Registering user: {}", userDTO);
        if (userDTO == null) {
            logger.error("UserDTO is null");
            return "error";
        }

        if (userDTO.getUserMail() == null || userDTO.getUserMail().isEmpty()) {
            logger.error("User email is null or empty");
            return "error";
        }

        if (userDTO.getUserId() == null || userDTO.getUserId().isEmpty()) {
            logger.error("User ID is null or empty");
            return "error";
        }

        try {
            if (idCheck(userDTO.getUserId())) {
                logger.info("User ID already exists: {}", userDTO.getUserId());
                return "duplicate_id";
            }

            if (emailCheck(userDTO.getUserMail())) {
                logger.info("Email already exists: {}", userDTO.getUserMail());
                return "duplicate_email";
            }

            saveUser(userDTO);

            logger.info("User registered successfully: {}", userDTO.getUserId());
            return "ok";
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during user registration: ", e);
            return "duplicate";
        } catch (Exception e) {
            logger.error("Unexpected error during user registration: ", e);
            return "error";
        }
    }

    @Transactional
    public void saveUser(UserDTO userDTO) {
        UserEntity userEntity = UserEntity.fromDTO(userDTO);
        try {
            userRepository.save(userEntity);
            logger.info("User saved successfully: {}", userDTO.getUserId());
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while saving user: ", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while saving user: ", e);
            throw e;
        }
    }

    public UserDTO getUserById(Long id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (userEntityOptional.isPresent()) {
            logger.info("User found with id: {}", id);
            return UserDTO.toUserDTO(userEntityOptional.get());
        } else {
            logger.warn("User not found with id: {}", id);
            return null;
        }
    }

    @Transactional
    public void updateUser(UserDTO userDTO) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userDTO.getId());
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            userEntity.setUserName(userDTO.getUserName());
            userEntity.setUserPw(userDTO.getUserPw());
            userRepository.save(userEntity);
            logger.info("User updated successfully: {}", userDTO.getId());
        } else {
            logger.warn("User not found with id: {}", userDTO.getId());
        }
    }

    public String findUserIdByEmail(String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUserMail(email);
        return userEntityOptional.map(UserEntity::getUserId).orElse(null);
    }

    public boolean sendPasswordByEmail(String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUserMail(email);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            emailService.sendPasswordEmail(email, userEntity.getUserPw());
            return true;
        } else {
            return false;
        }
    }

}
    