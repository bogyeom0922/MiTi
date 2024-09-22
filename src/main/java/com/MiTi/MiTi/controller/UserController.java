package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 정보 가져오기
    @GetMapping("/{provider}/{providerId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String provider, @PathVariable String providerId) {
        Optional<UserDTO> memberDTO = userService.getMemberById(provider, providerId);
        if (memberDTO.isPresent()) {
            return new ResponseEntity<>(memberDTO.get(), HttpStatus.OK);
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
