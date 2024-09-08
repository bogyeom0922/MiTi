package com.MiTi.MiTi.controller.mypage;

import com.MiTi.MiTi.dto.LikeDto;
import com.MiTi.MiTi.dto.UserDTO;
import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.repository.LikeRepository;
import com.MiTi.MiTi.service.AlbumService;
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
    private final AlbumService albumService;

    public LikeController(LikeService likeService, LikeRepository likeRepository, UserService userService, AlbumService albumService) {
        this.likeService = likeService;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.albumService = albumService;
    }

    @GetMapping("/mypage/like/{userId}")
    public String list(@PathVariable String userId, Model model) {
        List<LikeDto> likeDtoList = likeService.getLikeListByUserId(userId);
        model.addAttribute("postList", likeDtoList);
        model.addAttribute("userId", userId);

        UserDTO userDTO = userService.getUserById(Long.parseLong(userId));
        model.addAttribute("user", userDTO);

        return "mypage/mypage_like";
    }

    @PostMapping("/mypage/like/album/toggle")
    @ResponseBody
    public String toggleAlbumLike(@RequestParam("userId") String userId, @RequestParam("albumId") Long albumId) {
        boolean isLiked = likeService.toggleAlbumLike(userId, String.valueOf(albumId)); // Long을 String으로 변환
        return isLiked ? "liked" : "unliked";
    }

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

    @PostMapping("/mypage/like/track/toggle")
    @ResponseBody
    public String toggleTrackLike(@RequestParam("userId") String userId, @RequestParam("trackId") Long trackId) {
        boolean isLiked = likeService.toggleTrackLike(userId, String.valueOf(trackId)); // Long을 String으로 변환
        return isLiked ? "liked" : "unliked";
    }

    @GetMapping("/mypage/like/check")
    @ResponseBody
    public boolean isLiked(@RequestParam("userId") String userId, @RequestParam("albumId") Long albumId) {
        return likeService.isAlbumLikedByUser(userId, String.valueOf(albumId)); // Long을 String으로 변환
    }

    @GetMapping("/mypage/like/track/check")
    @ResponseBody
    public boolean isTrackLiked(@RequestParam("userId") String userId, @RequestParam("trackId") Long trackId) {
        return likeService.isTrackLikedByUser(userId, String.valueOf(trackId)); // Long을 String으로 변환
    }

    @GetMapping("/album/{albumId}")
    public String getAlbumDetail(@PathVariable Long albumId, @RequestParam("userId") String userId, Model model) {
        Album album = albumService.getAlbumById(albumId);
        model.addAttribute("firstAlbum", album);

        boolean isLikedAlbum = likeService.isAlbumLikedByUser(userId, String.valueOf(albumId)); // Long을 String으로 변환
        model.addAttribute("isLikedAlbum", isLikedAlbum);

        return "album/album_detail";
    }
}
