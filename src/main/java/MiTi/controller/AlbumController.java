package MiTi.controller;

import com.MiTi.dto.AlbumDto;
import com.MiTi.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
public class AlbumController {

    @Autowired
    private RecordService recordService;
/*
    @GetMapping("/albums/{user_id}")
    public List<AlbumDto> getAlbums(@PathVariable Integer user_id) {
        return recordService.getAlbumsByUserId(user_id);
    }

 */
}