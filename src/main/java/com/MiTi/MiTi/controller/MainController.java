package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {

    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String intro() {
        return "intro";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/main/{id}")
    public String mainPage(@PathVariable("id") Long userId, Model model) {
        UserDTO userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);

        return "main";
    }
}
