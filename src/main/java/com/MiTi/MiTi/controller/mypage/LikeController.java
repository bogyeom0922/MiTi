package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.repository.LikeRepository;
import com.MiTi.MiTi.service.LikeService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LikeController {

    private final LikeService likeService;
    private final LikeRepository likeRepository;
    private final UserService userService;

    public LikeController(LikeService likeService, LikeRepository likeRepository, UserService userService) {
        this.likeService = likeService;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @GetMapping("/mypage/like/{userId}")
    public String list(@PathVariable String userId, Model model, @PathVariable("userId") Long user) {
        List<LikeDto> likeDtoList = likeService.getLikeListByUserId(String.valueOf(userId));
        model.addAttribute("postList", likeDtoList);
        model.addAttribute("userId", userId);

        UserDTO userDTO = userService.getUserById(user);
        model.addAttribute("user", userDTO);
        return "mypage/mypage_like";
    }

    // 게시글 삭제 (물리적 삭제)

    @DeleteMapping("/mypage/like/{id}")
    @ResponseBody
    public String deleteLike(@PathVariable Long id) {
        try {
            likeService.deleteLike(id);
            return "success";
        } catch (Exception e) {
            return "failure";
        }
    }


}
