package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.MyCommentDto;
import com.MiTi.MiTi.repository.MyCommentRepository;
import com.MiTi.MiTi.service.MyCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MyCommentController {

    private final MyCommentService myCommentService;
    private final MyCommentRepository myCommentRepository;

    public MyCommentController(MyCommentService myCommentService, MyCommentRepository myCommentRepository) {
        this.myCommentService = myCommentService;
        this.myCommentRepository = myCommentRepository;
    }

    @GetMapping("/mypage/comment/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<MyCommentDto> myCommentDtoList = myCommentService.getCommentListByUserId(String.valueOf(userId));
        model.addAttribute("commentList", myCommentDtoList);
        model.addAttribute("userId", userId);
        return "mypage/mypage_comment";
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



    // 게시글 삭제 (물리적 삭제)

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
