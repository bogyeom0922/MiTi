package com.MiTi.MiTi.controller;


import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        String result = userService.registerUser(userDTO);
        switch (result) {
            case "ok":
                return ResponseEntity.ok("User registered successfully");
            case "duplicate_id":
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User ID already exists");
            case "duplicate_email":
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            case "duplicate":
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry");
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
        }
    }
}

