package com.MiTi.MiTi.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlayController {

    @GetMapping("/play")
    public String play() {
        return "play/under"; // templates/under.html 파일을 반환합니다.
    }
}