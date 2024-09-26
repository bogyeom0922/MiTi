package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 정보 가져오기
    @GetMapping("/{providerId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String providerId, Model model) {
        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);

        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 모델에 사용자 정보 추가
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // 회원 정보 저장하기
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.saveUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
