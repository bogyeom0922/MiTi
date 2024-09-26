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

    @GetMapping("/mypage/record/{providerId}")
    public String list(@PathVariable String providerId, Model model) {

        List<RecordDto> recordDtoList = recordService.getRecordListByProviderId(String.valueOf(providerId));
        model.addAttribute("recordList", recordDtoList);


        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 사용자 정보를 모델에 추가

            // 좋아요 목록을 가져와서 모델에 추가
            // model.addAttribute("likeList", likeService.getLikesForUser(userDTO.getProviderId()));
            return "mypage/mypage_record"; // 적절한 뷰 이름 반환
        }

        // 사용자 정보가 없는 경우, 적절한 처리를 해주셔야 합니다.
        return "error"; // 또는 다른 적절한 경로로 리디렉션
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