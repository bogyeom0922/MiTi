package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.service.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/mypage/record")
    public String list(Model model) {
        List<RecordDto> recordDtoList = recordService.getRecordList();
        model.addAttribute("postList", recordDtoList);
        return "mypage_record";
    }
}
