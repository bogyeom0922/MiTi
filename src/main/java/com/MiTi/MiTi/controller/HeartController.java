package com.MiTi.MiTi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HeartController {

    @GetMapping("/heart") //"/heart" 경로에 대한 GET 요청을 처리
    public String index() {
        return "signup/logo"; // "logo"라는 이름의 뷰를 반환
    }
}
