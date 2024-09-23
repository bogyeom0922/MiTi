package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.RecordDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.RecordService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Controller
public class RecordController {

    private final RecordService recordService;
    private final UserService userService;

    public RecordController(RecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    @GetMapping("/mypage/record/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<RecordDto> recordDtoList = recordService.getRecordListByUserId(String.valueOf(userId));
        model.addAttribute("recordList", recordDtoList);
        model.addAttribute("userId", userId);

        Optional<UserDTO> userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);
        return "mypage/mypage_record";
    }



    @PostMapping("/api/record")
    public ResponseEntity<String> recordMusic(@RequestBody RecordDto recordDto) {
        try {
            recordService.recordMusic(recordDto);
            return ResponseEntity.ok("Music recorded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error recording music");
        }
    }
}
