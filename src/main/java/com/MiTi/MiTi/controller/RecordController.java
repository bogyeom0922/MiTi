package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.service.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/mypage/record/{user_id}")
    public String list(@PathVariable Integer user_id, Model model) {
        List<RecordDto> recordDtoList = recordService.getRecordListByUserId(user_id);
        model.addAttribute("postList", recordDtoList);
        return "mypage_record";
    }
}
