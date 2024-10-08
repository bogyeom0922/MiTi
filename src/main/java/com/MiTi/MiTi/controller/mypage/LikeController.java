package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.MyCommentDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
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


    @GetMapping("/mypage/like/{providerId}")
    public String list(@PathVariable String providerId, Model model) {

        List<LikeDto> likeDtoList = likeService.getLikeListByUserId(String.valueOf(providerId));
        model.addAttribute("likeList", likeDtoList);

        Optional<UserDTO> userDTOOptional = userService.getUserById(providerId);
        if (userDTOOptional.isPresent()) {
            UserDTO userDTO = userDTOOptional.get();
            model.addAttribute("user", userDTO); // 사용자 정보를 모델에 추가

            // 좋아요 목록을 가져와서 모델에 추가
            // model.addAttribute("likeList", likeService.getLikesForUser(userDTO.getProviderId()));
            return "mypage/mypage_like"; // 적절한 뷰 이름 반환
        }

        // 사용자 정보가 없는 경우, 적절한 처리를 해주셔야 합니다.
        return "error"; // 또는 다른 적절한 경로로 리디렉션
    }

    //좋아요 삭제
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

    // 앨범 전체에 좋아요/좋아요 취소
    @PostMapping("/mypage/like/album/toggle")
    @ResponseBody
    public String toggleAlbumLike(@RequestParam("albumDetail") String detail, @RequestParam("providerId") String providerId) {
        // albumId는 곡의 ID를 나타내므로, albumId를 통해 곡을 좋아요/취소
        boolean isLiked = albumService.toggleAlbumLike(providerId, detail);
        return isLiked ? "liked" : "unliked";
    }


    @PostMapping("/mypage/like/album/toggleTrack")
    @ResponseBody
    public String toggleTrackLike(@RequestParam("albumId") Long albumId, @RequestParam("providerId") String providerId) {
        boolean isLiked = albumService.toggleTrackLike(providerId, albumId);
        return isLiked ? "liked" : "unliked";
    }

}