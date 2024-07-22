package com.MiTi.MiTi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HeartController {

    @GetMapping("/heart")
    public String index() {
        return "logo";
    }
}
