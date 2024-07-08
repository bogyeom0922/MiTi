package com.MiTi.MiTi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MiTiController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}