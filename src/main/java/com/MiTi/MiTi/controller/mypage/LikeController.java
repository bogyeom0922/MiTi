package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.service.AlbumService;
import com.MiTi.MiTi.service.LikeService;
import com.MiTi.MiTi.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;
    private final AlbumService albumService;

    public LikeController(LikeService likeService, UserService userService, AlbumService albumService) {
        this.likeService = likeService;
        this.userService = userService;
        this.albumService = albumService;
    }

    @GetMapping("/mypage/like/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<LikeDto> likeDtoList = likeService.getLikeListByUserId(userId);
        model.addAttribute("likeList", likeDtoList);
        model.addAttribute("userId", userId);

        Optional<UserDTO> userDTO = userService.getUserById(userId);
        model.addAttribute("user", userDTO);

        return "mypage/mypage_like";
    }

    // 앨범 전체에 좋아요/좋아요 취소
    @PostMapping("/mypage/like/album/toggle")
    @ResponseBody
    public String toggleAlbumLike(@RequestParam("albumId") String albumId, @RequestParam("userId") String userId) {
        // albumId는 곡의 ID를 나타내므로, albumId를 통해 곡을 좋아요/취소
        boolean isLiked = albumService.toggleAlbumLike(userId, albumId);
        return isLiked ? "liked" : "unliked";
    }


    @PostMapping("/mypage/like/album/toggleTrack")
    @ResponseBody
    public String toggleTrackLike(@RequestParam("albumId") String albumId, @RequestParam("userId") String userId) {
        boolean isLiked = albumService.toggleTrackLike(userId, albumId);
        return isLiked ? "liked" : "unliked";
    }

}