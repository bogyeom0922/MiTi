package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.RecordService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class RecordController {

    private final RecordService recordService;
    private final UserService userService;

    public RecordController(RecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    @GetMapping("/mypage/record/{userId}")
    public String list(@PathVariable Integer userId, Model model, @PathVariable ("userId") Long user) {
        List<RecordDto> recordDtoList = recordService.getRecordListByUserId(String.valueOf(userId));
        model.addAttribute("postList", recordDtoList);
        model.addAttribute("userId", userId);
        UserDTO userDTO = userService.getUserById(user);
        model.addAttribute("user", userDTO);
        return "mypage/mypage_record";
    }
}
