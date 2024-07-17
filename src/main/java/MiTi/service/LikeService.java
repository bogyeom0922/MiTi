
package MiTi.service;

import com.MiTi.dto.LikeDto;
import com.MiTi.entity.Like;
import com.MiTi.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public LikeService(LikeRepository likeRepository, EntityManager entityManager) {
        this.likeRepository = likeRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public List<LikeDto> getLikeList() {
        List<Like> likeList = likeRepository.findAll();
        List<LikeDto> likeDtoList = new ArrayList<>();

        for (Like like : likeList) {
            LikeDto likeDTO = LikeDto.builder()
                    .user_id(like.getUser_id())
                    .album_id(like.getAlbum_id())
                    .isLike(like.getIsLike())
                    .build();
            likeDtoList.add(likeDTO);
        }
        return likeDtoList;
    }

    @Transactional
    public void addLike(LikeDto likeDto) {
        Like like = likeDto.toEntity();
        likeRepository.save(like);
    }

    @Transactional
    public void deleteLike(Integer userId, Integer albumId) {
        Like like = likeRepository.findByUserIdAndAlbumId(userId, albumId);
        if (like != null) {
            likeRepository.delete(like);
            entityManager.detach(like); // 영속성 컨텍스트에서 엔티티 제거
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found for user " + userId + " and album " + albumId);
        }
    }
}
