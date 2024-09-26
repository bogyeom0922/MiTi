package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.MyCommentDto;
import com.MiTi.MiTi.dto.PlaylistDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.repository.MyCommentRepository;
import com.MiTi.MiTi.service.MyCommentService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class MyCommentController {

    private final MyCommentService myCommentService;
    private final MyCommentRepository myCommentRepository;
    private final UserService userService;

    public MyCommentController(MyCommentService myCommentService, MyCommentRepository myCommentRepository, UserService userService) {
        this.myCommentService = myCommentService;
        this.myCommentRepository = myCommentRepository;
        this.userService = userService;
    }

    @GetMapping("/mypage/comment/{providerId}")
    public String list(@PathVariable String providerId, Model model) {
        List<MyCommentDto> myCommentDtoList = myCommentService.getMyCommentListByProviderId(String.valueOf(providerId));
        model.addAttribute("commentList", myCommentDtoList);


        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 사용자 정보를 모델에 추가

            return "mypage/mypage_comment"; // 적절한 뷰 이름 반환
        }

        // 사용자 정보가 없는 경우, 적절한 처리를 해주셔야 합니다.
        return "error"; // 또는 다른 적절한 경로로 리디렉션
    }

    // 댓글 수정 요청 처리
    @PutMapping("/comment/{id}")
    public ResponseEntity<String> updateMyComment(@PathVariable Long id, @RequestBody MyCommentDto myCommentDto) {
        try {
            myCommentService.updateMyComment(id, myCommentDto);
            return ResponseEntity.ok("Comment updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update comment");
        }
    }

    // w댓글 삭제 (물리적 삭제)

    @DeleteMapping("/mypage/comment/{id}")
    @ResponseBody
    public String deleteMyComment(@PathVariable Long id) {
        try {
            myCommentService.deleteMyComment(id);
            return "success";
        } catch (Exception e) {
            return "failure";
        }
    }

}
