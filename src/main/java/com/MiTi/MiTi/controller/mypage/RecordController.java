package com.MiTi.MiTi.controller.mypage;

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

    @GetMapping("/mypage/record/{userId}")
    public String list(@PathVariable Integer userId, Model model) {
        List<RecordDto> recordDtoList = recordService.getRecordListByUserId(String.valueOf(userId));
        model.addAttribute("postList", recordDtoList);
        model.addAttribute("userId", userId);
        return "mypage/mypage_record";
    }
}
